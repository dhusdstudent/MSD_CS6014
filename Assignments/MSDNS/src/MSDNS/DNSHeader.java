package MSDNS;

import java.io.*;
//Header(ID/flags) -> Question -> Answer -> Author -> Extra



public class DNSHeader {
    private final int id;
    private final int flags;
    private final int questionPart;
    private final int answerPart;
    final int authorPart;
    final int extraPart;

//---------------------------Constructor------------------------------
    public DNSHeader(int id, int flags, int questionPart, int answerPart, int authorPart, int extraPart) {
        this.id = id;
        this.flags = flags;
        this.questionPart = questionPart;
        this.answerPart = answerPart;
        this.authorPart = authorPart;
        this.extraPart = extraPart;
    }

//---------------------------Helper functions------------------------------
    //read bytes berfore parsing.
    static byte[] readBytes(InputStream is) throws IOException {
        byte[] data = new byte[12];
        int bytesRead = 0;
        int result = 0;

        while (bytesRead < 12) {
            result = is.read(data, bytesRead, 12 - bytesRead);
            if (result == -1) {
                throw new EOFException();
            } bytesRead += result;
        } return data;
    }

    static void makeShort(OutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

//---------------------------Main functions------------------------------
    public static DNSHeader decodeHeader(InputStream inStream) throws IOException {
        byte[] data = readBytes(inStream);

        int id = ((data[0] & 0xFF) << 8 ) | (data[1] & 0xFF); //interpret first pair, set as id
        int flags = (data[2] & 0xFF) << 8 | (data[3] & 0xFF); //interpret 2nd pair, set as flag
        int questionPart = (data[4] & 0xFF) << 8 | (data[5] & 0xFF); //interpret 3rd pair, set as question
        int answerPart = (data[6] & 0xFF) << 8 | (data[7] & 0xFF); //interpret 4th pair, set as answer
        int authorPart = (data[8] & 0xFF) << 8 | (data[9] & 0xFF); //interpret 5th pair, set as authority
        int extraPart = (data[10] & 0xFF) << 8 | (data[11] & 0xFF); //interpret 6th pair, set as extra
        //total 12 bytes

        return new DNSHeader(id, flags, questionPart, answerPart, authorPart, extraPart); //build obj using data
    }

    static DNSHeader buildHeaderForResponse(DNSMessage request, DNSMessage response){
        int flags = request.header.getFlags();
        flags |= 0x8000; //This is for QR
        flags |= 0x0080; //This is for RA
        flags &= 0x7DFF; // This just clears
        return new DNSHeader(request.header.getID(), flags, request.header.getQuestionCount(), response.answers.length, 0, response.extra.length);
    }

    void writeBytes(OutputStream output) throws IOException {
        makeShort(output, id);
        makeShort(output, flags);
        makeShort(output, questionPart);
        makeShort(output, answerPart);
        makeShort(output, authorPart);
        makeShort(output, extraPart);
    }

    @Override
    public String toString(){
        return "DNSHeader{" + "id=" + id + "flags=" + flags + "questionPart=" + questionPart + "answerPart=" +
                answerPart + "authorPart=" + authorPart + "extraPart=" + extraPart + '}';
    }

//-------------------Methods to return variables------------------------------

    public int getQuestionCount() {
        return questionPart;
    }

    public int getAnswerCount() {
        return answerPart;
    }

    public int getFlags() {
        return flags;
    }

    public int getID() {
        return id;
    }
}
