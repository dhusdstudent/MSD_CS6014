package MSDNS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

public class DNSRecord {

    String[] domain;
    int type;
    int responseClass;
    int total;
    byte[] responseData;
    Date createdDate;


    static DNSRecord decodeRecord(InputStream inStream, DNSMessage message) throws IOException {
        DNSRecord record = new DNSRecord();

        record.domain = message.readDomainName(inStream).toArray(new String[0]);
        record.type = shortFromInput(inStream);
        record.responseClass = shortFromInput(inStream);
        record.total = (int)readInput(inStream); //recasting to make sure its def 32 bits

        int length = shortFromInput(inStream);
        record.responseData = inStream.readNBytes(length);

        record.createdDate = new Date();
        return record;
    }
    void writeBytes(ByteArrayOutputStream output, HashMap<String, Integer> hMap) throws IOException {
        DNSMessage.writeDomainName(output, hMap, domain);
        makeShort(output, type);
        makeShort(output, responseClass);
        output.write((total >> 24) & 0xFF);
        output.write((total >> 16) & 0xFF);
        output.write((total >> 8) & 0xFF);
        output.write((total) & 0xFF);

        makeShort(output, responseData.length);
        output.write(responseData);
    }

    boolean isExpired(){
        long nowTime = System.currentTimeMillis();
        long expTime = createdDate.getTime() + (total * 1000L);
        return nowTime >= expTime;
    }

    private static void makeShort(OutputStream out, int value) throws IOException {
        DNSHeader.makeShort(out, value);
    }

    static int shortFromInput(InputStream input) throws IOException {
       int high = input.read();
       int low = input.read();
       return (high << 8) | low;
    }

    private static long readInput(InputStream input) throws IOException {
        long a =  input.read();
        long b = input.read();
        long c = input.read();
        long d = input.read();
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    @Override
    public String toString(){
        return String.join(".", domain) + " type=" + type + " total=" + total + " length=" + responseData.length;
    }
}
