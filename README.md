# What's this?

Session register which holds the talks for JavaZone. Used by [Cake-redux](https://github.com/javaBin/cake-redux),  [Submit](https://github.com/javaBin/submit) and other JavaZone systems.

# Start the app locally

- Run the class `SparkStart`. 
- You need a property file if you want to save to a local postgres database etc. 
- See `Configuration` for available properties. 
- Just add the property file path as the first argument when starting.

# Import EMS talks

- Run the class `EmsImporter`. 
- Here you need a property file as well.
- See `EmsImportConfig` for the available properties

# Application deployment to AWS

## First time: set up software and configure credentials

Install `aws` and `eb` command line tools, as well as `ansible`:

```
brew install awscli
brew install aws-elasticbeanstalk
brew install ansible
```

Edit/add the file `~/.aws/credentials` and add these lines

```
[javabin]
aws_access_key_id = <ADD YOURS HERE>
aws_secret_access_key = <ADD YOURS HERE>
```

## Every time: deploy the app to AWS

`./deploy.sh sleepingPillCore-<env>`

The deploy needs the ansible vault password to be able to decrypt the property file. Ask around to get it :)

# Cloud tips and tricks :)

## SSH to the instance

You need the ssh key. Get the files `aws-eb` and `aws-eb.pub` from someone who have them already, and place them in `~/.ssh`

Then you just do:
```
eb ssh sleepingPillCore-<env>
```

- Logs: `cd /var/log && tail -f web*.log nginx/*log`
- App files: `cd /var/app/current/`

## App property files for AWS

To edit which properties are used for deployment to AWS, edit the files in the `config` folder:

```
ansible-vault edit config/<env>.properties.encrypted
```

You need the vault password for this. Ask around to get access to it :)

## Create a new environment

You could probably just use test/prod which exists. 

If you need a new, do this and follow the instructions to get an environment up and running
```
eb create --region eu-central-1 --profile javabin
```

You need to setup a database + property files for backend + DNS as well. For now, this is a "manual process". Ask around... ;)