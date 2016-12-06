# SleepingPillCore

Work in progress.

Session register for Cake and Submitit.

# Deployment to AWS

## First time: configure AWS

Install `aws` and `eb` command line tools:

```
brew install awscli
brew install aws-elasticbeanstalk
```


Edit/add the file `~/.aws/credentials` and add these lines

```
[javabin]
aws_access_key_id = <ADD YOURS HERE>
aws_secret_access_key = <ADD YOURS HERE>
```

Initialize your environment: 
```
eb init --region eu-central-1 --profile javabin
```

Just select the existing sleepingPillCore environment

## Every time: deployment

`./deploy.sh`

## Creating a new environment

You could probably just use test/prod which exists. If you need a new, do this and follow the instructions
```
eb create --region eu-central-1 --profile javabin
```