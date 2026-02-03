import re #importing regular expression module for standard things we use a lot

inFile = "trace1.txt"
#importing the file that I got via pinging in the console

outFile = "myAverages.txt"
#The file that we're going to write to. IP_address average_delay

#Opens the file from above, puts it in 'read ' mode via "r" assigns it to the variable 'x'
#Opens the output file, puts it in 'write' mode via "w" assigns it to the variable 'y'

with open (inFile, "r") as x, open(outFile, "w") as y:
    #NOTE: "With" closes file when complete

    for line in x:
        #Reads the traceroute line by line

        ipMatch = re.search(r"\(([d.] +)\)", line)

        #Uses search function to look for pattern of "\(([\d\.]+)\)"
        #\(([\d\.]+)\) = (, digits [multiple], ) [i.e. IP address)

        if not ipMatch:
            #will skip line and move to the next if not a match
            continue

        IP = ipMatch.group(1)
        #Specifies the data where the 1 is (i.e. inside parenthases)
        #Refers to \(([\d\.]+)\) pattern above

        delayTime = re.findall(r"(\d+\.\d+)\s*ms", line)
        #Locates delay indications in line via r"(\d+\.\d+)\s*ms"
        #r"(\d+\.\d+)\s*ms" is digit, decimal, digit, space, text 'ms'
        #At this point saves as a string

        delayTime = [float(d) for d in delayTime]
        #Converts string from a document into float

        if len(delayTime) == 0:
            #Will skip again if no match.
            #len = number of items in a particular container
            continue

        avgDelay = sum(delayTime) / len(delayTime)
        #adds number, divides by delays

        y.write(f"{IP} {avgDelay}\n")
        #writes to output file in format [IP address] [delay time]