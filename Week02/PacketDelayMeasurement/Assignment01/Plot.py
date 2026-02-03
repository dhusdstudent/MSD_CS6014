import matplotlib.pyplot as plt #This is apparently a plotting library

#Declaration of a function in python
def loadedData(file):

    #Creates list ips to store strings, creates delayed to store numbers (no variable definition)
    ips = []
    delayed = []

    #Opens the 'file' which I can change from either trace1 or trace2
    with open(file) as f:

        #naming file variable as 'f'

        for line in f:

            #Going through each line by line
            #Splitting the line into two buckets by space; one for first value one for second (ip versus delay time)

            ip, delayed = line.split()

            #Adding value to either the ips or delayed list. Convert 'delayed' value into a float.

            ips.append(ip)
            delayed.append(float(delayed))

            #Returns both lists. Python can return more than one in a function?
    return ips, delayed

#Loading the experimental plotted docs
ips1, delays1 = loadedData("averages1.txt")
ips2, delays2 = loadedData("averages2.txt")

#Doing the part where it gets plotted
#Breaking out by curves
plt.plot(ips1, delays1, marker='o', label="Time 1")
plt.plot(ips2, delays1, marker='x', label="Time 2")

#Putting labels onto the graph
plt.xlabel("Hop IP Address")
plt.ylabel("Average Delay (ms)")
plt.title("Traceroute Delay per Hop")

#Making the graph's legend
plt.legend()
plt.xticks(rotation=90)
plt.tight_layout()

#Showing the graph
plt.show()

#Saving the graph
plt.savefig("traceroutePlot.pdf")

#----------------Analysis----------------
#QUESTION:
#Suppose one of the three traceroute delay values between the source and a given router hop turns out to
# be unusually high. What are two possible causes for this unusually high delay?
#ANSWER:
#Some suggestions I would have would be that it could be based on:
#   1. The amount of traffic currently being serviced by that area.
#   2. I believe the type of connection could impact the speed (and therefore delay) of the transfer. For instance,
# a physical (wired) connection could result in a lesser delay than a wireless one.