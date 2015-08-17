charging-system-api
=============

An charging system in Finagle with Scala

Overview
-----------
### POST /transactions
Create new transaction. Required fields are:

* customerId
* startTime
* volume
* activeStarting, and should be after current time

#### Example
```
curl -H 'content-type: application/json' http://localhost:8088/transactions -d '{ "customerId": "john", "startTime": "2015-10-28T09:34:17Z", "endTime": "2015-10-28T16:45:13Z", "volume": 32.03 }'
```

### POST /tariffs
Create new tariff. Required fields are:

* startFee
* hourlyFee
* feePerkWh
* activeStarting, and should be after current time

#### Example
```
curl -H 'content-type: application/json' http://localhost:8088/tariffs -d '{ "startFee": 0.20, "hourlyFee": 1.00, "feePerKWh": 0.25, "activeStarting": "2015-08-18T06:00:00Z" }'
```

### GET /overview
Overview of charge sessions.

#### Example
```
curl http://localhost:8088/overview
```
Response:
```
pete,2014-10-27T13:32:14Z,2014-10-27T14:32:14Z,13.21,4.50 john,2014-10-28T09:34:17Z,2014-10-28T16:45:13Z,32.03,14.70
```
With content-type:`"text/csv"`

### GET /invoices/`<year>`/`<month number>`/`<customer name>`
Customer invoice.

#### Example
```
curl http://localhost:8088/invoices/2015/10/john
```
Response:
```
Dear john,

In October 2015, you have charged:
from 2015-10-28 09:34 to 2015-10-28 16:45: 32.03 kWh @ â‚¬ 15.39

Total amount: â‚¬ 15.39

Kind regards, Your dearest mobility provider, The Venerable Inertia
```
With content-type:`"text/txt"`


How to Run
-----------

### Vagrant
1. Install [`vagrant`](http://docs.vagrantup.com/v2/installation/), and
   [`ansible`](http://docs.ansible.com/ansible/intro_installation.html).
```
$ git clone git@github.com:leonmaia/charging-system-api.git
$ cd ./path/to/repo
$ vagrant up --provision
```

### Local
```
$ git clone git@github.com:leonmaia/charging-system-api.git
$ cd ./path/to/repo
$ java -Dfile.encoding=UTF-8 -jar target/scala-2.11/charging-system-api-assembly-1.0.jar
```

### Local - Dev Mode
```
$ git clone git@github.com:leonmaia/charging-system-api.git
$ cd ./path/to/repo
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
And open the `index.html` file at ./path/to/repo/target/scala-2.11/scoverage-report
