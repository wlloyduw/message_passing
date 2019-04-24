/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lambda;

import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faasinspector.register;
import java.nio.charset.Charset;
/**
 * uwt.lambda_test::handleRequest
 * @author wlloyd
 */
public class MessagePassing implements RequestHandler<Request, Response>
{
    static String CONTAINER_ID = "/tmp/container-id";
    static Charset CHARSET = Charset.forName("US-ASCII");
    static String nodedata = "";
    static int calls = 0;
    
    
    // Lambda Function Handler
    public Response handleRequest(Request request, Context context) {
        // Register function to start timing
        int totalCalls = 0;
        register reg = new register();

        // Create logger
        LambdaLogger logger = context.getLogger();
        
        // Register logger 
        reg.setLogger(logger);

        //stamp container with uuid
        Response r = reg.StampContainer();
        
        // *********************************************************************
        // Implement Lambda Function Here
        // *********************************************************************
        if (!request.getSleep())
            calls ++;
        
        // Pass data, only if this node is just now receieving it...
        if ((request.getCurrentround() <= request.getRounds()) && (!nodedata.matches(request.getData())) && (!request.getSleep()))
        {
            // Persist the data locally
            nodedata=request.getData();
            
            if (request.getCurrentround() < request.getRounds()) {
                Request newRequest = new Request();
                newRequest.setCurrentround(request.getCurrentround()+1);
                newRequest.setRounds(request.getRounds());
                newRequest.setNodespread(request.getNodespread());
                newRequest.setData(request.getData());
                newRequest.setSleep(false);
                final ObjectMapper mapper = new ObjectMapper();
                try
                {
                    logger.log("new request JSON=" + mapper.writeValueAsString(newRequest));
                }
                catch (JsonProcessingException jpe)
                {
                    logger.log("Error displaying newRequest object=" + jpe.toString());
                }

                final MessagePassingService messagePassingService = LambdaInvokerFactory.builder()
                        .lambdaClient(AWSLambdaClientBuilder.defaultClient())
                        .build(MessagePassingService.class);                

                // TO DO
                // Make this multithreaded
                for (int i=0;i<request.getNodespread();i++)
                {
                    totalCalls = totalCalls + 1;
                    logger.log("Nodespread " + i+1 + " of " + request.getNodespread()); 
                    Response newResponse = messagePassingService.callMessagePassing(newRequest);
                    logger.log("lambda function invoke complete");
                    logger.log("function-response=" + newResponse.toString());
                    totalCalls = totalCalls + newResponse.getTotalCalls();
                }
            }
        }
        else
        {
//            if ((request.getCurrentround() == request.getRounds()))
//            {
//                // Persist the data locally for the last round
//                nodedata=request.getData();
//            }
            
            logger.log("Round is " + request.getCurrentround() + " of " + request.getRounds());
            logger.log("Request to pass data, but local node has data='" + nodedata + "'");
            try
            {
                if (request.getSleep())
                {
                    logger.log("SLEEEPING!!!");
                    Thread.sleep(10000);
                }
                //r.setCalls(calls);
                // Reset calls counter to 0 so message passing can be retested
                //calls = 0;
            }
            catch (InterruptedException ie)
            {
                logger.log("Interrupted while sleeping in no data pass mode!");
            }
        }
        
        // Set return result in Response class, class is marshalled into JSON
        r.setValue(nodedata);
        r.setCalls(calls);
        r.setTotalCalls(totalCalls);
        reg.setRuntime();
        return r;
    }
    
    public interface MessagePassingService {

        @LambdaFunction(functionName = "messagepass")
        Response callMessagePassing(Request input);
    }
    
    // int main enables testing function from cmd line
    public static void main (String[] args)
    {
        Context c = new Context() {
            @Override
            public String getAwsRequestId() {
                return "";
            }

            @Override
            public String getLogGroupName() {
                return "";
            }

            @Override
            public String getLogStreamName() {
                return "";
            }

            @Override
            public String getFunctionName() {
                return "";
            }

            @Override
            public String getFunctionVersion() {
                return "";
            }

            @Override
            public String getInvokedFunctionArn() {
                return "";
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return new LambdaLogger() {
                    @Override
                    public void log(byte[] string) {
                        System.out.println("LOG:" + string.toString());
                    }
                    @Override
                    public void log(String string) {
                        System.out.println("LOG:" + string);
                    }                    
                };
            }
        };
        
        // Create an instance of the class
        MessagePassing lt = new MessagePassing();
        
        // Create a request object
        Request req = new Request();
        
        // Grab the name from the cmdline from arg 0
        String name = (args.length > 0 ? args[0] : "");
        
        // Load the name into the request object
        req.setData(name);

        // Report name to stdout
        System.out.println("cmd-line param name=" + req.getData());
        
        // Run the function
        Response resp = lt.handleRequest(req, c);
        
        // Print out function result
        System.out.println("function result:" + resp.toString());
    }
}
