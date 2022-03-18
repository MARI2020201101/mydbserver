package com.mariworld.raw;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileHandler {

    private RandomAccessFile dbfile;

    //파일을 연다 lsof기능
    public FileHandler(final String dbFileName) throws FileNotFoundException {
        this.dbfile = new RandomAccessFile(dbFileName, "rw");
    }

    //데이터를 파일에 쓴다.
    public boolean add(String name, int age, String address, String carPlateNumber, String description) throws IOException {
        this.dbfile.seek(this.dbfile.length()); //파일 맨 마지막을 찾는다(append 해서 쓰기 위함)

        //어떤 데이터를 쓸것인가?
        //isDeleted + record Length
        // + nameLen + name
        // + addressLen + address
        // + carPlateLen + carPlatenum
        // descriptionLen + description

        //얼마만큼의 length 를 file에 새로 쓸것인가? -> Record의 크기를 계산해야한다.
        int length = 4 + //name length. int는 4바이트
                    name.length() + //name
                    4 + //age int 4바이트
                    4 + //address length
                    address.length() + //address
                    4 +  //carPlateNum len
                    carPlateNumber.length() + //carPlateNum
                    4 + //description len
                    description.length(); //description

        this.dbfile.writeBoolean(false); //is not deleted record임을 표시
        this.dbfile.writeInt(length); //이 레코드의 길이(메타데이터). 읽을때 얼마만큼의 byte을 읽어야 하는지 알아야한다

        this.dbfile.writeInt(name.length()); //name 의 길이 int로 쓸것임
        this.dbfile.write(name.getBytes(StandardCharsets.UTF_8));

        this.dbfile.writeInt(age);

        this.dbfile.writeInt(address.length());
        this.dbfile.write(address.getBytes(StandardCharsets.UTF_8));

        this.dbfile.writeInt(carPlateNumber.length());
        this.dbfile.write(carPlateNumber.getBytes(StandardCharsets.UTF_8));

        this.dbfile.writeInt(description.length());
        this.dbfile.write(description.getBytes(StandardCharsets.UTF_8));

        return true; //데이터 저장.
    }

    //읽을 레코드의 인덱스 넘버를 넘길것임
    public Person readRow(int rowNumber) throws IOException {
        byte[] row = this.readRowRecord(rowNumber);
        Person person = new Person();
        //데이터를 읽는다
        //해당 데이터 크기만큼의 스트림열어서

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(row));

        int nameLength = stream.readInt();
        byte[] b = new byte[nameLength];
        stream.read(b);
        person.name = new String(b);

        person.age = stream.readInt();

        b = new byte[stream.readInt()];
        stream.read(b);
        person.address = new String(b);

        b = new byte[stream.readInt()];
        stream.read(b);
        person.carPlateNumber = new String(b);

        b = new byte[stream.readInt()];
        stream.read(b);
        person.description = new String(b);

        return person;
    }

    private byte[] readRowRecord(int rowNumber) throws IOException {
        this.dbfile.seek(0);//인덱스로 레코드를 찾고.
        if(this.dbfile.readBoolean()) return new byte[0]; //지워진 레코드임

        this.dbfile.seek(rowNumber+1);
        int recordLength = this.dbfile.readInt(); //레코드의 길이.
        this.dbfile.seek(rowNumber+5);
        byte[] data = new byte[recordLength];
        this.dbfile.read(data);

        return data;
    }

    //파일을 닫는다
    public void close() throws IOException {
        this.dbfile.close();
    }
}
