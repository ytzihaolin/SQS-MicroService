import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import java.net.*;
import java.io.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;


public class SQSservice {

    @SuppressWarnings({ "unchecked", "resource", "resource" })
	public static void main(String[] args) throws Exception {
    	System.out.println("Specify the routing table file:");
    	//form the routing table
    	Scanner fileloc=new Scanner(System.in);
    	HashMap<String,String> routingTable=new HashMap<String,String>();
    	File routingtable = new File(fileloc.nextLine());
    	Scanner input=new Scanner(routingtable);
    	while(input.hasNextLine()){
    		String[] temp=input.nextLine().split(" ");
    		routingTable.put(temp[0], temp[1]);
    	}
    	if(routingTable.size()==0) return;
    	
    	System.out.println("Sqs service is running");
    	while(true){
    		Httphandler sendob=new Httphandler();  //object helping to communicate with Sean's API	
            AWSCredentials credentials = null;
            try {
                credentials = new ProfileCredentialsProvider().getCredentials();
            } catch (Exception e) {
                throw new AmazonClientException(e);
            }

            AmazonSQS sqs = new AmazonSQSClient(credentials);
            Region usEast1 = Region.getRegion(Regions.US_EAST_1);
            sqs.setRegion(usEast1);
            try {
                // Create a queue URL request
                String requestQueueUrl = sqs.getQueueUrl(new GetQueueUrlRequest("APIcourse")).getQueueUrl();
                String responseQueueUrl = sqs.getQueueUrl(new GetQueueUrlRequest("ResponseQueue")).getQueueUrl();
                // Receive messages
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(requestQueueUrl);
                while(true){//keep polling from the queue to get all the messages
    	            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    	            if(messages.size()==0) break;//break loop if there's none
    	            for (Message message : messages) {	   
    	            	System.out.println("new messages received!");
    	                JSONParser jsonParser = new JSONParser();
    	                System.out.println(message.getBody().toString());
    	                JSONObject Rjson=(JSONObject) jsonParser.parse(message.getBody());
    	                JSONObject headerJson=(JSONObject) jsonParser.parse(Rjson.get("header").toString());

    	                String url=routingTable.get(headerJson.get("ServiceName"));
    	                String Operation=(String) headerJson.get("Operation");
    	                String SSN=(String) headerJson.get("SSN");
    	                String MessageId = message.getMessageId();
    	                JSONObject bodyJson=(JSONObject) jsonParser.parse(Rjson.get("body").toString());
    	                
    	                url=url+SSN;
    	                //send the request and get the response from Sean's API and parse it
    	                JSONObject responseJson=new JSONObject();
    	                if(Operation==null){
    	                	System.out.println("no operation");
    	                }else{
    	                	 switch(Operation.toUpperCase()){
    		                	case "GET":{
    		                		 System.out.println("perform get");
    		                		 responseJson.put("result", sendob.sendGet(url));
    		                		 break;
    		                	}
    		                	case "POST":{
    		                		System.out.println("perform post");
    		                		responseJson.put("result", sendob.sendPost(url,bodyJson));
    		                		break;
    		                	}
    		                	case "DELETE":{
    		                		System.out.println("perform delete");
    		                		responseJson.put("result", sendob.sendDelete(url));
    		                		break;
    		                	}
    		                	case "PUT":{
    		                		System.out.println("perform put");
    		                		responseJson.put("result", sendob.sendPut(url,bodyJson));
    		                		break;
    		                	}
    		                	default:{
    		                		responseJson.put("result", "No Operation specified");
    		                		break;
    		                	}
    		                }
    	                }
    	                responseJson.put("MessageId", MessageId);
    	                System.out.println(responseJson.toJSONString());
    	                //form a response message and send it to the response queue.
    	                sqs.sendMessage(new SendMessageRequest(responseQueueUrl, responseJson.toJSONString()));
    	                //delete the received message from the queue.
    	                sqs.deleteMessage(new DeleteMessageRequest(requestQueueUrl, message.getReceiptHandle()));
    	            }
                }
            } catch (AmazonServiceException ase) {
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Error Message: " + ace.getMessage());
            }
            try{
            	Thread.sleep(1000);
            }catch(Exception ex){
            	System.out.println(ex);
            }
    	}
    	
    }
    
    

}