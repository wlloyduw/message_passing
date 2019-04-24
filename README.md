# message_passing
# Message Passing on AWS Lambda using nested Lambda calls

# Example Run: 

# First initialize 10 nodes (Lambda run-time containers)
$ ./init.sh 10

Wed Apr 24 12:54:27 PDT 2019
Setting up nodes: nodecount=10 using: runsperthread=1 threads=10 totalruns=10

uuid,host,thedata,calls,totalcalls,uses,avgruntime_cont,avgsstuntime_cont,avglatency_cont
"27f570ea-9982-4358-ae36-40ec440fa43b",1556131454,"",0,0,1,11570,10021,1549
"56897618-99f1-4ab8-a1a6-9a1d8dee48a5",1556134881,"",0,0,1,11569,10021,1548
"bdde87be-c825-42ea-af47-67d8b4f72697",1556131511,"",0,0,1,11577,10021,1556
"41dd1b97-df9e-470b-9b3b-ad97d656dc37",1556132150,"",0,0,1,11555,10020,1535
"cd0738a8-e7df-41c9-a0b3-8c94cd851918",1556132486,"",0,0,1,11579,10020,1559
"862ce990-2c7b-4c32-8944-4b03c55e1a8b",1556132349,"",0,0,1,11585,10020,1565
"9c3dc26b-47d4-47f4-8547-25d0e5b52cee",1556132746,"",0,0,1,11539,10021,1518
"c0505de1-e474-40c8-b1fe-99e7f8f87686",1556132370,"",0,0,1,11618,10022,1596
"49a91d42-53fe-4cab-8f72-5ecfc3a6988b",1556131690,"",0,0,1,11700,10022,1678
"fa583436-517e-4fe3-b45f-a1eaaefcc2e8",1556131857,"",0,0,1,11724,10071,1653

containers,newcontainers,recycont,hosts,recyvms,avgruntime,avgssruntime,avglatency,runs_per_container,runs_per_cont_stdev,runs_per_host,runs_per_host_stdev,totalcost
10,10,0,10,0,11602,10026,1576,1.000,0.000,1.000,0.000,$0.0050

# Next, use the messagepass.sh script to spread data to all 10 nodes.
# messagepass.sh takes two parameters, the # of rounds, and the node spread per round:

wlloyd@dione:~/git/message_passing/faas_inspector/lambda/java_template/test$ ./messagepass.sh 

Usage ./messagepass.sh {# of rounds} {node spread per round}

# Here we try 3 rounds, where each round tries to send 20 messages
# This takes 12.684s and creates a total of 100 Lambda calls plus the initial one

wlloyd@dione:~/git/message_passing/faas_inspector/lambda/java_template/test$ ./messagepass.sh 3 20
{"data":"The\u0020Data","rounds":3,"currentround":1,"nodespread":20}
Invoking Lambda function using AWS CLI

real	0m12.684s
user	0m0.264s
sys	0m0.052s

AWS CLI RESULT:
{"cpuType":"Intel(R) Xeon(R) Processor @ 2.50GHzstepping","uuid":"fa583436-517e-4fe3-b45f-a1eaaefcc2e8","error":"","vmuptime":1556131857,"runtime":12039,"newcontainer":0,"value":"The Data","calls":1,"totalCalls":100}

# Next we check how well the data was spread across the 10 nodes
# Only 5 of 10 nodes received the data
# With few rounds and many messages per round, the data is not spread very well

wlloyd@dione:~/git/message_passing/faas_inspector/lambda/java_template/test$ ./check.sh 10
Wed Apr 24 12:56:21 PDT 2019
Checking data spread nodecount=10 runsperthread=1 threads=10 totalruns=10

"The Data"
"The Data"
""
"The Data"
"The Data"
""
"The Data"
""
""
""
uuid,host,data,calls,totalcalls,uses,avgruntime_cont,avgsstuntime_cont,avglatency_cont
"c0505de1-e474-40c8-b1fe-99e7f8f87686",1556132370,"The Data",28,0,1,10927,10000,927
"49a91d42-53fe-4cab-8f72-5ecfc3a6988b",1556131690,"The Data",35,0,1,10931,10001,930
"bdde87be-c825-42ea-af47-67d8b4f72697",1556131511,"",0,0,1,10948,10001,947
"862ce990-2c7b-4c32-8944-4b03c55e1a8b",1556132349,"The Data",22,0,1,10942,10001,941
"fa583436-517e-4fe3-b45f-a1eaaefcc2e8",1556131857,"The Data",1,0,1,10944,10000,944
"56897618-99f1-4ab8-a1a6-9a1d8dee48a5",1556134881,"",0,0,1,10930,10001,929
"9c3dc26b-47d4-47f4-8547-25d0e5b52cee",1556132746,"The Data",15,0,1,10940,10001,939
"27f570ea-9982-4358-ae36-40ec440fa43b",1556131454,"",0,0,1,10975,10001,974
"cd0738a8-e7df-41c9-a0b3-8c94cd851918",1556132486,"",0,0,1,10952,10002,950
"41dd1b97-df9e-470b-9b3b-ad97d656dc37",1556132150,"",0,0,1,10968,10001,967
Current time of test=1556135793

containers,newcontainers,recycont,hosts,recyvms,avgruntime,avgssruntime,avglatency,runs_per_container,runs_per_cont_stdev,runs_per_host,runs_per_host_stdev,totalcost
10,0,0,10,0,10946,10001,945,1.000,0.000,1.000,0.000,$0.0050

