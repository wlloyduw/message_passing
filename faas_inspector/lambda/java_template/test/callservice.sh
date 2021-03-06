#!/bin/bash

# JSON object to pass to Lambda Function
json={"\"data\"":"\"The\u0020Data3\",\"rounds\"":9,\"currentround\"":1,\"nodespread\"":5}
#json={"\"data\"":"\"\",\"rounds\"":0,\"currentround\"":0,\"nodespread\"":0,\"sleep\"":true"}
echo $json

#echo "Invoking Lambda function using API Gateway"
#time output=`curl -s -H "Content-Type: application/json" -X POST -d  $json {INSERT API GATEWAY URL HERE}`

#echo ""
#echo "CURL RESULT:"
#echo $output
#echo ""
#echo ""

echo "Invoking Lambda function using AWS CLI"
time output=`aws lambda invoke --invocation-type RequestResponse --function-name messagepass --region us-east-1 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "AWS CLI RESULT:"
echo $output
echo ""







