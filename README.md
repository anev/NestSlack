# Nest slack integration

The program integrates nest platform with slack. 
It gives an opportunity to see thermostats' temperature value in slack,
set up a thershold and get notification if threshold is crossed.

![Nest Slack Bot](NestSlackBot.png?raw=true)

## Building

To build jar just run `sbt assembly`, you need java8, sbt 0.13.6+.

A jar file `target/scala-2.11/NestSlack-assembly-0.1.jar` will be produced. 

## Configuration

Where is several settings you have to set as environment variables
 * `SLACK_TOKEN`
 * `NEST_TOKEN`

and also one optional parameter
 * `SLACK_CHANNEL` - default is `general`
 

## Running as jar

It's the easies way, don't forget to set up env.
```
export SLACK_TOKEN="..."
export NEST_TOKEN="..."
java -jar target/scala-2.11/NestSlack-assembly-0.1.jar
```

That's it! Help message will be printed to slack channel.

## Running using docker

 * build docker container: `docker build -t anev/nestslack .`
 * and then run it `docker run -e SLACK_TOKEN="..." -e NEST_TOKEN="..." anev/nestslack`
