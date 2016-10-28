# SleepingPillCore

Work in progress.

Session register for Cake and Submitit.

Pushing to Elastic Beanstalk
--------
Install Elastic Beanstalk cli
http://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3.html

Generate my key
```ssh-keygen -f ~/.ssh/javabin-aws```

Attach the key to a new group
```eb init sleepingpill --region eu-west-1 --keyname javabin-aws --platform java-8```


```eb create pill-prod --region eu-west-1 --instance_type t2.micro --keyname javabin-aws --platform java-8 --scale 1```

Set up AWS credentials
----------------------
Credentials
create aws credentials file ~/.aws/credentials

'''
[default]
aws_access_key_id= AKI......
aws_secret_access_key=....
region=eu-west-1
'''