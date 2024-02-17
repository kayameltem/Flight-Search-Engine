import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Flight {
    private String id;
    private String dept;
    private String arr;
    private Date deptDate;
    private long duration;
    private int price ;
    private String company;

    public Flight(String[] flight) {
        try {
            this.id = flight[0];
            this.company = id.substring(0,2);
            // formatting the depthDate
            this.deptDate = new SimpleDateFormat("dd/MM/yyyy HH:mm E").parse(flight[2]);
            // calculating the duration in miliseconds
            String[] dummy = flight[3].split(":");
            this.duration = Integer.valueOf(dummy[0]) * 3600000 + Integer.valueOf(dummy[1]) * 60000 ;
            this.price = Integer.valueOf(flight[4]);
            // splitting the departure and arrival airports
            String[] aux = flight[1].split("->");
            this.dept = aux[0];
            this.arr = aux[1];
        }
        catch (ParseException e) {
            e.printStackTrace();

        }
    }

    public String getDept() {
        return dept;
    }
    public String getArr() {
        return arr;
    }
    public Date getDeptDate() {
        return deptDate;
    }
    public int getPrice() {
        return price;
    }
    public String getCompany() {
        return company;
    }
    // calculates the arrival date and returns
    public Date getArrDate() {
        Date arrDate = new Date(deptDate.getTime() + duration);
        return arrDate;
    }
    // overriding toString() method
    @Override
    public String toString() {
        return String.format("%s\t%s->%s",id,dept,arr);
    }
}
