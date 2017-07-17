# What's this?

Session register which holds the talks for JavaZone. Used by [Cake-redux](https://github.com/javaBin/cake-redux),  [Submit](https://github.com/javaBin/submit) and other JavaZone systems.

Contains both a public API that can be consumed by anyone, and a private API used internally to submit talks and review/select talks.

# Public API

You can consume the published events from JavaZone by using the public JSON-api. There are no need for any authentication, just call the API using your favorite HTTP-client.

**API for talks in production:**
To fetch the conferences in the system use the following:
https://sleepingpill.javazone.no/public/allSessions

This will give the following json:
```javascript
 {
 "conferences": [
     {
     "name": "JavaZone 2016",
     "slug": "javazone_2016"
     },
     {
     "name": "JavaZone 2017",
     "slug": "javazone_2017"
     },
     ....
 ]}
```
To fetch the sessions for each conference add the conference slug to the url, for example:

```
https://sleepingpill.javazone.no/public/allSessions/javazone_2017
```

The API returns a list of the published talks for the year. The following format are an example:

```javascript
{
  "sessions": [
    {
      "sessionId": "", // The ID of the session
      "title": "", // Title of the talk
      "abstract": "", // The full description of the talk
      "intendedAudience": "", // Who the speaker wants to come see their talk
      "language": "", // Language of the talk. Can be one of "no", "en"
      "format": "", // The format of the talk. Can be one of "presentation", "lightning-talk", "workshop"
      "level": "", // Difficulty level of the talk. Can be one of "beginner", "intermediate", "advanced"
      "keywords": ["list", "of", "keywords"], // Keywords classifying the talk
      "speakers": [ // A list of speakers for the talk
        {
          "name": "", // Name of the speaker
          "bio": "", // What the speaker said about him/herself
          "twitter": "" // The speaker's Twitter account
        },
        // ... more speakers here
      ]
    },
    // ... more talks here
  ]
}
```

**Important:** As there are some changes each year concerning which fields are included, the consumers of the API should handle any missing fields gracefully. For example, the field "twitter" on the speaker are new in 2017, and the field "level" is missing in 2016. The fields "title" and "abstract", and the field "name" on the speaker can be assumed to be present.

## What should you call the fields in your GUI?

The following table lists the different fields, and what name we are using to describe this. Feel free to change this if needed (due to short space etc.), but do it intentionally at least ;)

| Field | Name | Field limits | Description given to speaker |
| --- | --- | --- | --- |
| sessionId | _internal field, not for display_ | - |
| title | Title | freetext | Select an expressive and snappy title that captures the content of your talk without being too long. Remember that the title must be attractive and should make people curious. |
| abstract | Description | freetext | Give a concise description of the content and goals of your talk. Try not to exceed 300 words, as shorter and more to-the-point descriptions are more likely to be read by the participants. |
| intendedAudience  | Expected Audience and Code Level | freetext | Who should attend this session? How will the participants benefit from attending? Please indicate how code will factor into your presentation (for example "no code", "code in slides" or "live coding"). |
| language | Language | en / no | Which language will you be holding the talk in? It is permitted to use English in your slides, even though you may be talking in Norwegian, but you should write the rest of the abstract in the language you will speak in. We generally recommend that you hold the talk in the language you are most comfortable with. |
| format | Presentation format | presentation / lightning-talk / workshop | In which format are you presenting your talk? Presentation, lightning talk or workshop? |
| level | Experience level | beginner / intermediate / advanced | Who is your talk pitched at? Beginners, Experts or perhaps those in between? |
| keywords | Keywords | list of strings | Suggest up to five keywords that describe your talk. These will be used by the program committee to group the talks into categories. We reserve the right to edit these suggestions to make them fit into this years selected categories. |
| speaker -> name | Speakers name | freetext | Your name |
| speaker -> bio | Short description of the speaker | freetext | Short description of the speaker (try not to exceed 150 words) |
| speaker -> twitter | Twitter handle | freetext | Twitter handle, starting with an @ |


# Private API

Check out the Java class `no.java.sleepingpill.core.controller.HttpPaths` to see all the paths that can be called. 

Check out the test class `no.java.sleepingpill.core.ExampleClient` for example on how to use the APIs.

-------------------------

# Developing SleepingPill


## Start the app locally

- Run the class `SparkStart`. 
- You need a property file if you want to save to a local postgres database etc. 
- See `Configuration` for available properties. 
- Just add the property file path as the first argument when starting.

## Import EMS talks

- Run the class `EmsImporter`. 
- Here you need a property file as well.
- See `EmsImportConfig` for the available properties

# Deploying SleepingPill

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

## Cloud tips and tricks :)

### Downloading a dump of the database
You can use postgres pg_dump to download the sleepingpill database. You need to open for access to the database. Login to the aws console, locate the security group for the rds instance and add an inbound rule for your ip. Run:
pg_dump -h <dbserver> --username <dbUser> --dbname <dbname> --format custom --blobs --verbose >filename.backup

You can find the values for dbserver,dbuser,dbname and dbpassword in the app property file (as described below).
Please remember to delete your custom inbound rule from the security group after you are done :)

### SSH to the instance

You need the ssh key. Get the private and public key (`javabin` and `javabin.pub`) from someone who have them already, and place them in `~/.ssh`

Then you just do:
```
eb ssh sleepingPillCore-<env>
```

- Logs: `cd /var/log && tail -f web*.log nginx/*log`
- App files: `cd /var/app/current/`

### App property files for AWS

To edit which properties are used for deployment to AWS, edit the files in the `config` folder:

```
ansible-vault edit config/<env>.properties.encrypted
```

You need the vault password for this. Ask around to get access to it :)

### Create a new environment

You could probably just use test/prod which exists. 

If you need a new, do this and follow the instructions to get an environment up and running
```
eb create --region eu-central-1 --profile javabin
```

You need to setup a database + property files for backend + DNS as well. For now, this is a "manual process". Ask around... ;)