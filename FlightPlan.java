import java.util.ArrayList;

public class FlightPlan {
    private int price;
    private long duration;
    private ArrayList<Flight> plan;

    public FlightPlan(){
        plan = new ArrayList<Flight>();
        price = 0;
        duration = 0;
    }

    public int getPrice() {
        return price;
    }

    // converts the duration which is in miliseconds to hour:minute format and returns
    public String getDuration() {
        // calculates hour(s)
        String hour = String.valueOf((int) (duration / 3600000));
        // calculates minute(s)
        String minute = String.valueOf((int) (duration % 3600000) / 60000);
        if(hour.length() < 2) {
            hour = "0" + hour;
        }
        if(minute.length() <2) {
            minute = "0" + minute;
        }
        return String.format("%s:%s",hour,minute);
    }

    public ArrayList<Flight> getPlan() {
        return plan;
    }

    //fills the plan variable and calculates total price and total duration in miliseconds.
    public void setPlan(ArrayList<Flight> flights) {
        plan.addAll(flights);
        for (Flight flight: flights) {
            price += flight.getPrice();
        }
        duration = plan.get(plan.size()-1).getArrDate().getTime() - plan.get(0).getDeptDate().getTime();
    }

    // overriding the toString method
    @Override
    public String toString() {
        String str ="" ;
        for (Flight f: plan) {
            str += f;
            if (f != plan.get(plan.size()-1)){
                str+= "||";
            }
        }
        return String.format("%s\t%s/%s\n",str,getDuration(),price);
    }
}
