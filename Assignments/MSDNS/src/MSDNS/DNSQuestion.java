package MSDNS;

//This class represents a client request.

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class DNSQuestion {
    String[] domain;
    int type;
    int qclass;

    public DNSQuestion(String[] domain, int type, int qclass) {
        this.domain = domain;
        this.type = type;
        this.qclass = qclass;
    }

    static DNSQuestion decodeQuestion(InputStream inStream, DNSMessage message) throws IOException {
        String[] domain = message.readDomainName(inStream).toArray(new String[0]);
        int type = readShort(inStream);
        int quest = readShort(inStream);
        return new DNSQuestion(domain, type, quest);
    }

    private static int readShort(InputStream inStream) throws IOException {
        int high = inStream.read();
        int low = inStream.read();
        return ((high & 0xFF) << 8) | (low& 0xFF);
    }

    void writeBytes(ByteArrayOutputStream byteOut, HashMap<String,Integer> domainNameLocations) throws IOException {
       DNSMessage.writeDomainName(byteOut, domainNameLocations, domain);
        makeShort(byteOut, type);
        makeShort(byteOut, qclass);
    }

    @Override
   public String toString(){
        return String.join(".", domain) + "type=" + type + "class=" + qclass;
    }

    @Override
    public boolean equals(Object question){
        if (this == question) return true;
        if (!(question instanceof DNSQuestion)) return false;
        DNSQuestion other = (DNSQuestion) question;
        return Arrays.equals(domain, other.domain) && type == other.type && qclass == other.qclass;
           }

    @Override
    public int hashCode(){
        return Objects.hash(Arrays.hashCode(domain), type, qclass);
    }

    static void makeShort(OutputStream out, int value) throws IOException {
        DNSHeader.makeShort(out, value);
    }

}
