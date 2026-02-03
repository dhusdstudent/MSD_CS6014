# I picked Oslo for this assignment! > ping www.helsinki.fi > PingFinland.txt EG:
# PING www.uio.no (129.240.118.130): 56 data bytes
# 64 bytes from 129.240.118.130: icmp_seq=0 ttl=38 time=152.809 ms
# 339 packets transmitted, 339 packets received, 0.0% packet loss
# round-trip min/avg/max/stddev = 150.164/155.117/221.362/5.540 ms

#Regex library python
import re

pingFile = "PingOslo.txt"

#creates variable for rtts
rttsList = []

with open(pingFile, "r") as pf:
    for line in pf:
        match = re.search(r"time=([\d.]+)\s*ms", line)
        #looks for the specific line a la "time=152.809 ms"

        if match:
            rttItem = float(match.group(1))
            rttsList.append(rttItem)
            #if it finds a match, it will turn into a float and add to match.group(#)

minimumRTT = min(rttsList)
#sets the minimum for the purposes of having a baseline

queueTime = []
for rtt in rttsList:
    queueTime.append(rtt-minimumRTT)
#behavior to account for the queuing time delays

queueingAverage = sum(queueTime) / len(queueTime)
#Goes on to compute an average in queuing delays



#----------ANALYSIS----------
# As per the assignment, we can assume that the processing delays, transmission delays, and propogation delays are all
# constant. For that reason, we can approximate the queuing delay by computing the difference between each one's RTT and
# the minimum RTT alone, and by averaging these we can arrive at the amount of delay that'd occur during a packet's
# round trip.