package MSDNS;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DNSMessage {
    private final byte[] bytes; //byte array containing the complete message in this class. You'll need it to handle the compression technique described above
    public DNSRecord[] extra;
    DNSHeader header; //the DNS Header
    DNSQuestion[] questions; //an array of questions
    DNSRecord[] answers; //an array of answers
    DNSRecord[] auth; //an array of "authority records" which we'll ignore

    public DNSMessage(byte[] bytes) {
        this.bytes = bytes;
    }

    static DNSMessage decodeMessage(byte[] bytes) throws IOException {
        DNSMessage message = new DNSMessage(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        message.header = DNSHeader.decodeHeader(bais);

        message.questions = new  DNSQuestion[message.header.getQuestionCount()];
        for (int i = 0; i < message.questions.length; i++){
            message.questions[i] = DNSQuestion.decodeQuestion(bais, message);
        }

        message.answers = new  DNSRecord[message.header.getAnswerCount()];
        for (int i = 0; i < message.answers.length; i++){
            message.answers[i] = DNSRecord.decodeRecord(bais, message);
        }

        message.auth = new  DNSRecord[message.header.authorPart];
        for (int i = 0; i < message.auth.length; i++){
            message.auth[i] = DNSRecord.decodeRecord(bais, message);
        }

        message.extra = new DNSRecord[message.header.extraPart];
        for (int i = 0; i < message.extra.length; i++){
            message.extra[i] = DNSRecord.decodeRecord(bais, message);
        }

        return message;
    }
    List<String> readDomainName(InputStream input) throws IOException {
        List<String> domainNames = new ArrayList<>();

        while(true) {
            int length = input.read();
            if (length == -1) throw new EOFException();
            if (length == 0) break;
            if ((length & 0xC0) == 0xC0) {
                int secondByte = input.read();
                int pointer = (length & 0x3f) << 8 | (secondByte & 0xFF);
                String[] contents = readDomainName(pointer);
                domainNames.addAll(Arrays.asList(contents));
                break;
            }

            byte[] nameBytes = input.readNBytes(length);
            domainNames.add(new String(nameBytes));
        }
        return domainNames;
    }

    String[] readDomainName(int firstByte) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes,  firstByte, bytes.length - firstByte);
        return readDomainName(bais).toArray(new String[0]);
    }

    static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers){
        DNSMessage message = new DNSMessage(null);
        message.questions = request.questions;
        message.answers = answers;
        message.extra = request.extra;

        message.header = DNSHeader.buildHeaderForResponse(request, message);
        return message;
    }

    public byte[] toBytes() {  //-- get the bytes to put in a packet and send back
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HashMap<String,Integer> domainNameLocations = new HashMap<>();
        try {
            header.writeBytes(baos);
            for (DNSQuestion question : questions) {
                question.writeBytes(baos, domainNameLocations);
            }
            for (DNSRecord answer : answers) {
                if(answer != null) {
                    answer.writeBytes(baos, domainNameLocations);
                }
            }
            if (extra != null) {
                for (DNSRecord extras : extra) {
                    extras.writeBytes(baos, domainNameLocations);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } return baos.toByteArray();
    }

    static void writeDomainName(ByteArrayOutputStream outStream, HashMap<String,Integer> domainLocations, String[] domainPieces) throws IOException {
        String domainName = String.join(".", domainPieces);

        if(domainLocations.containsKey(domainName)){
            int location = domainLocations.get(domainName);
            makeShort(outStream, 0xC000 | location);
            return;
        }
        domainLocations.put(domainName, outStream.size());
        for(String piece : domainPieces){
            byte[] nameBytes = piece.getBytes();
            outStream.write(nameBytes.length);
            outStream.write(nameBytes);
        }
        outStream.write(0);
    }

    String joinDomainName(String[] pieces) {
        //-- join  pieces of domain name w dots
            return String.join(".", pieces);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n");

        if (questions != null) {
            for (DNSQuestion question : questions) {
                sb.append("Q: ").append(question).append("\n");
            }
        }
        if (answers != null) {
            for (DNSRecord answer : answers) {
                sb.append("A: ").append(answer).append("\n");
            }
        }
        return sb.toString();
    }

    static void makeShort(OutputStream out, int value) throws IOException {
        DNSHeader.makeShort(out, value);
    }
}
