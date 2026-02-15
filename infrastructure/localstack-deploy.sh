#!/bin/bash


# Configuration
ENDPOINT="http://localhost:4566"
STACK_NAME="patient-management"

echo "Deploying CloudFormation stack to LocalStack..."


aws --endpoint-url=$ENDPOINT cloudformation delete-stack \
	--stack-name $STACK_NAME

aws --endpoint-url=$ENDPOINT cloudformation deploy \
	--stack-name $STACK_NAME \
	--template-file "./cdk.out/localstack.template.json"
	
aws --endpoint-url=$ENDPOINT elbv2 describe-load-balancers \
	--query "LoadBalancers[0].DNSName" --output text

if [ $? -ne 0 ]; then
    echo "Deployment failed. Fetching events..."
    aws --endpoint-url=$ENDPOINT cloudformation describe-stack-events --stack-name $STACK_NAME
fi