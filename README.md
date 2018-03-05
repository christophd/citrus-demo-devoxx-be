Demo for "Behavior driven integration with Cucumber and Citrus" ![Logo][1]
==============

This demo application uses the combination of [Citrus][2] and [Cucumber][3] for behavior driven development (BDD). 
The Cucumber tests use feature stories written in Gherkin syntax in combination with Citrus integration test capabilities.
 
DevoxxBE 2017
---------

This demo is used as an example project in the conference session [Behavior driven integration with Cucumber and Citrus][5]. 
The project demonstrates how to use Cucumber and Citrus for automated integration testing of message interfaces.

Objectives
---------

The voting demo application is a simple Spring boot web application. The app provides a Http REST interface for clients and browsers. 
In addition to that clients can use a JMS inbound destination for adding ne voting entries.
 
The automated testing shows the usage of both Cucumber and Citrus in combination. Step definitions are able to use *@CitrusResource* and *@CitrusEndpoint*
annotations for injecting a Citrus components such as endpoints and test runner instances. The test runner Java fluent API is then used in Cucumber steps 
to exchange messages via different message transports (Htp REST, JMS, Mail). We can still write normal step definition classes that use Gherkin annotations
(*@Given*, *@When*, *@Then*) provided by Cucumber.

The Cucumber tests use JUnit as unit testing framework and are executable from Java IDE or command line with Maven. 

Get started
---------

We start with adding some dependencies for Cucumber and Citrus to the Maven project:

    <!-- Citrus -->
    <dependency>
      <groupId>com.consol.citrus</groupId>
      <artifactId>citrus-core</artifactId>
      <version>${citrus.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.consol.citrus</groupId>
      <artifactId>citrus-java-dsl</artifactId>
      <version>${citrus.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.consol.citrus</groupId>
      <artifactId>citrus-http</artifactId>
      <version>${citrus.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.consol.citrus</groupId>
      <artifactId>citrus-cucumber</artifactId>
      <version>${citrus.version}</version>
      <scope>test</scope>
    </dependency>

    <!-- Cucumber -->
    <dependency>
      <groupId>io.cucumber</groupId>
      <artifactId>cucumber-core</artifactId>
      <version>${cucumber.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>io.cucumber</groupId>
      <artifactId>cucumber-junit</artifactId>
      <version>${cucumber.version}</version>
      <scope>test</scope>
    </dependency>

After that we set a special Cucumber object factory in the *cucumber.properties*

    cucumber.api.java.ObjectFactory=cucumber.runtime.java.CitrusObjectFactory
    
This object factory provided by Citrus enables the extensions needed for Cucumber to work with Citrus. This is dependency injection for Citrus 
related resources as well as special test preparations. No we can use a normal feature test using the Cucumber JUnit runner.

    @RunWith(Cucumber.class)
    @CucumberOptions(
            plugin = { "com.consol.citrus.cucumber.CitrusReporter" } )
    public class VotingFeatureIT {
    }

The *@RunWith* annotation tells JUnit to run this test with Cucumber. Also we set an optional Citrus reporter that will print some Citrus test results to the console for us.
The test feature file describes the user stories and scenarios using Gherkin syntax.

    Feature: Voting Http REST API
    
      Background:
        Given Voting list is empty
        And New voting "Do you like Belgian beer?"
        And voting options are "yes:no"
    
      Scenario: Create voting
        When client creates the voting
        Then client should be able to get the voting
        And the list of votings should contain "Do you like Belgian beer?"
    
      Scenario: Add votes
        When client creates the voting
        And client votes for "yes"
        Then votes should be
          | yes | 1 |
          | no  | 0 |
    
      Scenario: Top vote
        When client creates the voting
        And client votes for "no"
        Then votes should be
          | yes | 0 |
          | no  | 1 |
        And top vote should be "no"
    
      Scenario: Close voting
        Given reporting is enabled
        When client creates the voting
        And client votes for "yes" 3 times
        And client votes for "no" 2 times
        And client closes the voting
        Then participants should receive reporting mail
    """
    Dear participants,
    
    the voting '${title}' came to an end.
    
    The top answer is 'yes'!
    
    Have a nice day!
    Your Voting-App Team
    """
        
The steps executed are defined in a separate class where a Citrus test runner is used to build integration test logic.
The test steps call REST API operations as client and verify the response messages from the server. In addition to that Citrus
provides backend service simulation for JMS and Mail SMTP.

    public class VotingRestSteps {
    
        @CitrusEndpoint
        private HttpClient votingClient;
    
        @CitrusEndpoint
        private MailServer mailServer;
    
        @CitrusResource
        private TestRunner runner;
    
        @Given("^Voting list is empty$")
        public void clear() {
            runner.http(action -> action.client(votingClient)
                    .send()
                    .delete("/voting"));
    
            runner.http(action -> action.client(votingClient)
                    .receive()
                    .response(HttpStatus.OK));
        }
    
        @Given("^New voting \"([^\"]*)\"$")
        public void newVoting(String title) {
            runner.variable("id", "citrus:randomUUID()");
            runner.variable("title", title);
            runner.variable("options", buildOptionsAsJsonArray("yes:no"));
            runner.variable("closed", false);
            runner.variable("report", false);
        }
        
        @When("^(?:I|client) creates? the voting$")
        public void createVoting() {
            runner.http(action -> action.client(votingClient)
                .send()
                .post("/voting")
                .contentType("application/json")
                .payload("{ \"id\": \"${id}\", \"title\": \"${title}\", \"options\": ${options}, \"report\": ${report} }"));
    
            runner.http(action -> action.client(votingClient)
                .receive()
                .response(HttpStatus.OK)
                .messageType(MessageType.JSON));
        }
    
        @When("^(?:I|client) votes? for \"([^\"]*)\"$")
        public void voteFor(String option) {
            runner.http(action -> action.client(votingClient)
                    .send()
                    .put("voting/${id}/" + option));
    
            runner.http(action -> action.client(votingClient)
                    .receive()
                    .response(HttpStatus.OK));
        }
        
        [...]
    }    

Configuration
---------

In order to enable Citrus Cucumber support we need to specify a special object factory in *cucumber.properties*.
    
    cucumber.api.java.ObjectFactory=cucumber.runtime.java.CitrusObjectFactory
    
The object factory takes care on creating all step definition instances. The object factory is able to inject *@CitrusResource* and *@CitrusEndpoint*
annotated fields in step classes.
    
The endpoints are configured in a Spring bean Java configuration class. Here we define several Citrus endpoint components that are injected to the step classes.

    @Configuration
    public class CitrusEndpointConfig {
    
        @Bean
        public HttpClient votingClient() {
            return CitrusEndpoints.http()
                    .client()
                    .requestUrl("http://localhost:8080/rest/services")
                    .build();
        }
        
        @Bean
        public JmsEndpoint createVotingEndpoint() {
            return CitrusEndpoints.jms()
                    .asynchronous()
                    .connectionFactory(connectionFactory())
                    .destination("jms.voting.create")
                    .build();
        }
    
        @Bean
        public JmsEndpoint voteEndpoint() {
            return CitrusEndpoints.jms()
                    .asynchronous()
                    .connectionFactory(connectionFactory())
                    .destination("jms.voting.inbound")
                    .build();
        }
    
        @Bean
        public JmsEndpoint reportingEndpoint() {
            return CitrusEndpoints.jms()
                    .asynchronous()
                    .connectionFactory(connectionFactory())
                    .destination("jms.voting.report")
                    .build();
        }
    
        @Bean
        public ConnectionFactory connectionFactory() {
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
            activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
            return activeMQConnectionFactory;
        }
    
        @Bean
        public MailServer mailServer() {
            return CitrusEndpoints.mail()
                    .server()
                    .port(2222)
                    .autoStart(true)
                    .autoAccept(true)
                    .build();
        }
    }
    
All beans defined here are candidates for dependency injection using the *@CitrusEndpoint* annotation.

Run
---------

You can run the sample application on your local host. The application is built with Maven build tool. So you can compile, package and test the
sample with Maven on command line calling.
 
     mvn clean install -Dembedded=true
    
This executes the complete Maven build lifecycle. The embedded option automatically starts a Jetty web server
container before the integration test phase. The voting Spring Boot system under test is automatically deployed and started in this phase.
After that the Citrus test cases are able to interact with the voting application in the integration test phase.

During the build you will see Citrus performing some integration tests and Cucumber to print some test reports, too.
After the tests are finished the embedded Spring Boot infrastructure and the voting application are automatically stopped.

System under test
---------

The sample uses a small voting application as system under test. The application is a Spring Boot web application
that you can deploy on any web container. As we have already seen earlier in this README the Spring Boot application is automatically
started during the Maven build lifecycle when using the **embedded=true** option. This approach is fantastic 
when running automated tests in a continuous build.
  
Besides that you can start the voting application manually in order to access the web front end with a browser.  

You can start the sample voting application with this command.

     mvn -pl voting-app spring-boot:run

Point your browser to
 
    http://localhost:8080/

You will see the web UI of the voting application. Now you can play around with the web frontend and create some new votes.

The application uses some JMS interface for sending reports to a simulated backend. In case we need
to also start the ActiveMQ message broker for JMS message exchange. You enable JMS in the sample application by running.

    mvn -pl voting-app spring-boot:run -Dspring.profiles.active=jms
    
In a separate terminal run following command to start the ActiveMQ message broker:
    
    mvn -pl voting-app activemq:run

Now we are ready to execute some Citrus tests in a separate JVM.

Test execution
---------

Once the sample application is deployed and running locally as described before you can execute the Citrus test cases.
Open a separate command line terminal and navigate to the sample folder.

Execute all Citrus tests by calling

     mvn integration-test

You can also pick a single test by calling

     mvn integration-test -Ptest=VotingFeatureIT

You should see Citrus performing several tests with lots of debugging output in both terminals (sample application server
and Citrus test client). And of course green tests at the very end of the build.

Of course you can also start the Citrus tests from your favorite IDE.
Just start the Citrus test using the JUnit IDE integration in IntelliJ, Eclipse or Netbeans.

Further information
---------

For more information on Citrus see [citrusframework.org][2], Cucumber is located on [cucumber.io][3]. The Citrus Cucumber 
extension is described in this [reference manual][4].

 [1]: https://citrusframework.org/img/brand-logo.png "Citrus"
 [2]: https://citrusframework.org
 [3]: https://cucumber.io
 [4]: https://citrusframework.org/reference/html/cucumber.html
 [5]: https://cfp.devoxx.be/2017/talk/HCR-4774/Behavior_driven_integration_with_Cucumber_and_Citrus
