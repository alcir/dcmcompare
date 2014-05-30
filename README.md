dcmcompare
==========

###Introductin


Tool to query two PACS server and get differences between patients and related number of studies and instances
Details

It can be used in two ways:

    type 1: for each study on serverA search it on serverB
    type 2: query serverA and serverB then search the differences 

So, using type 1 if serverB is i.e. a long term server, it could contain studies from other servers, not only from serverA; at the same time I want to know if all studies on serverA are present on serverB.

Using type 2 I want to know if the two server are equal.

It can send results by mail (useful in crontab).

###Usage

    DcmCompare <date|fromdate-todate> <aet1>@<host1>:<port1> <aet2>@<host2>:<port2> [Options]

Examples:

    DcmCompare 20120101-20120131 QRSCP@localhost:11112 QRSCP2@otherhost:11112 -m MR -type 1 -mail
    DcmCompare 20120101 QRSCP@localhost:11112 QRSCP2@otherhost:11112 -m MR -type 1 -mail
    DcmCompare YESTERDAY QRSCP@localhost:11112 QRSCP2@otherhost:11112 -type 2
    DcmCompare TODAY QRSCP@localhost:11112 QRSCP2@otherhost:11112 -type 2 -m DR

To configure mail parameters, edit etc/mail.conf file

Date format for query is YYYYMMDD or a range YYYYMMDD-YYYYMMDD. 
