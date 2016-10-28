# SleepingPillCore

Work in progress.

Session register for Cake and Submitit.

Pushing to Elastic Beanstalk
--------
```ssh-keygen -f ~/.ssh/javabin-aws```

```eb init sleepingpill --region eu-west-1 --keyname javabin-aws --platform java-8```

```eb create pill-prod --region eu-west-1 --instance_type t2.micro --keyname javabin-aws --platform java-8 --scale 2```

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