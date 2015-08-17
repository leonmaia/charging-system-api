newmotion api
=============

An charging system in Finagle with Scala

Overview
-----------

How to Run
-----------

### Vagrant
1. Install [`vagrant`](http://docs.vagrantup.com/v2/installation/), and
   [`ansible`](http://docs.ansible.com/ansible/intro_installation.html).
```
$ git clone git@github.com:leonmaia/newmotion.git
$ cd ./path/to/newmotion/repo
$ vagrant up --provision
```

### Local
```
$ git clone git@github.com:leonmaia/newmotion.git
$ cd ./path/to/newmotion/repo
$ java -Dfile.encoding=UTF-8 -jar target/scala-2.11/newmotion-api-assembly-1.0.jar
```

### Local - Dev Mode
```
$ git clone git@github.com:leonmaia/newmotion.git
$ cd ./path/to/newmotion/repo
$ ./sbt run
```

Running tests
-----------

Tests are run using sbt.
```
$ ./sbt test
```
Or you can view a coverage report:
```
$ ./sbt cov
```
And open the `index.html` file at ./path/to/newmotion/repo/target/scala-2.11/scoverage-report
