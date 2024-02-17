import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database {
    private HashMap<String, ArrayList<Airport>> airports;  // City : Airports
    private HashMap<Airport, HashSet<Flight>> flights;     // Airport : Flights
    private ArrayList<FlightPlan> plans;
    ArrayList<FlightPlan> proper;

    Database(String airportList, String flightList,String command) {
        this.airports = new HashMap<String, ArrayList<Airport>>();
        this.flights = new HashMap<Airport, HashSet<Flight>>();
        FileIO file = new FileIO();
        ArrayList<String[]> airport = file.read(airportList);  // read Airport List
        ArrayList<String[]> flight = file.read(flightList);   // read Flight List
        ArrayList<String[]> commands = file.read(command);

        // Fill the airports map
        for (String[] s:airport) {
            airports.put(s[0], new ArrayList<Airport>());
            for (int i = 1; i < s.length ; i++) {
                Airport air = new Airport(s[i], s[0]);
                airports.get(s[0]).add(air);
                flights.put(air, new HashSet<Flight>());
            }
        }
        // Fill the flights map
        for(String[] s:flight) {
            addFlight(s);
        }
        // concatenate the command
        for (String[] s: commands) {
            String str = s[0];
            for (int i = 1; i < s.length; i++) {
                str = str + "\t" + s[i];
            }
            str += "\n";

            //if proper and paths lists are empty, fill them
            if (s.length > 2) {
                String[] arr = s[1].split("->");
                    printAll(arr[0], arr[1], s[2]);
                    listProper();
            }

            if (s[0].equals("listAll")) {
                file.output(str, plans);
            } else if (s[0].equals("listProper")) {
                file.output(str, proper);
            } else if (s[0].equals("listCheapest")) {
                file.output(str, listCheapest());
            } else if (s[0].equals("listQuickest")) {
                file.output(str, listQuickest());
            } else if (s[0].equals("listCheaper")) {
                file.output(str, listCheaper(Integer.valueOf(s[3])));
            } else if (s[0].equals("listQuicker")) {
                file.output(str, listQuicker(s[3]));
            } else if (s[0].equals("listExcluding")) {
                file.output(str, listExcluding(s[3]));
            } else if (s[0].equals("listOnlyFrom")) {
                file.output(str, listOnlyFrom(s[3]));
            } else {
                file.output(str, new ArrayList<FlightPlan>());
            }
        }
        try {
            file.getFile().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // finds proper flight plan(s)
    public void listProper(){
        proper = new ArrayList<FlightPlan>();
        ArrayList<FlightPlan> sortedbyDuration = new ArrayList<FlightPlan>();
        ArrayList<FlightPlan> sortedbyPrice = new ArrayList<FlightPlan>();
        // copy the all flights and sort by duration in ascending order
        sortedbyDuration.addAll(plans);
        sortedbyDuration.sort(Comparator.comparing(FlightPlan::getDuration));
        // copy the all flights and sort by price in ascending order
        sortedbyPrice.addAll(plans);
        sortedbyPrice.sort(Comparator.comparing(FlightPlan::getPrice));
        while(!(sortedbyDuration.isEmpty() || sortedbyPrice.isEmpty())){
            FlightPlan plan = sortedbyPrice.get(0);
            int index = sortedbyDuration.indexOf(plan);
            for (; index < sortedbyDuration.size(); index++) {
                FlightPlan p = sortedbyDuration.get(index);
                if (plan.getDuration().equals(p.getDuration())){
                    if((sortedbyPrice.size() == 1 ) || (sortedbyPrice.size() > 1) && sortedbyPrice.get(1).getPrice() >= p.getPrice() ) {
                        proper.add(p);
                    }
                }
                else if (plan.getPrice() == p.getPrice()) {
                    proper.add(p);
                }
                sortedbyPrice.remove(p);

            }
        }
    }
    // finds the cheapest flight plan(s)
    public ArrayList<FlightPlan> listCheapest(){
        ArrayList<FlightPlan> paths = new ArrayList<FlightPlan>();
        ArrayList<FlightPlan> cheapest = new ArrayList<FlightPlan>();
        // copy the all flights and sort by price in ascending order
        paths.addAll(plans);
        paths.sort(Comparator.comparing(FlightPlan::getPrice));
        if (!paths.isEmpty()) {
            int price = paths.get(0).getPrice();
            for (FlightPlan p : paths) {
                if (p.getPrice() != price) {
                    break;
                }
                cheapest.add(p);

            }
        }
        return cheapest;
    }
    // finds the quickest flight plan(s)
    public ArrayList<FlightPlan> listQuickest(){
        ArrayList<FlightPlan> paths = new ArrayList<FlightPlan>();
        ArrayList<FlightPlan> quickest = new ArrayList<FlightPlan>();
        // copy the all flights and sort by duration in ascending order
        paths.addAll(plans);
        paths.sort(Comparator.comparing(FlightPlan::getDuration));
        if (!paths.isEmpty()) {
            String str = paths.get(0).getDuration();
            for (FlightPlan p : paths) {
                if (!p.getDuration().equals(str)) {
                    break;
                }
                quickest.add(p);
            }
        }
        return quickest;
    }
    // finds cheaper flight plan(s) than given parameter
    public ArrayList<FlightPlan> listCheaper(int price){
        ArrayList<FlightPlan> cheaper = new ArrayList<FlightPlan>();
        for (FlightPlan p:proper) {
            if(p.getPrice() < price){
                cheaper.add(p);
            }
        }
        return cheaper;
    }
    //finds quicker flight(s) which have the arrival time before the given parameter
    public ArrayList<FlightPlan> listQuicker(String str){
        ArrayList<FlightPlan> quicker = new ArrayList<FlightPlan>();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm E").parse(str);
        for (FlightPlan p: proper) {
            if(p.getPlan().get(p.getPlan().size()-1).getArrDate().before(date)){
                quicker.add(p);
            }
        }
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return quicker;
    }
    //finds flight plan(s) which are not include the flight of iven company
    public ArrayList<FlightPlan> listExcluding(String company){
        ArrayList<FlightPlan> excluding = new ArrayList<FlightPlan>();
        for (FlightPlan p:proper) {
            boolean bool = true;
            for (Flight f: p.getPlan()) {
                if(f.getCompany().equals(company)){
                    bool = false;
                    break;
                }
            }
            if (bool){
                excluding.add(p);
            }
        }
        return excluding;
    }
    // finds flight plan(s) which consists of only the flights of given company
    public ArrayList<FlightPlan>  listOnlyFrom(String company){
        ArrayList<FlightPlan> onlyfrom = new ArrayList<FlightPlan>();
        for (FlightPlan p:proper) {
            boolean bool = true;
            for (Flight f: p.getPlan()) {
                if(!f.getCompany().equals(company)){
                    bool = false;
                    break;
                }
            }
            if(bool){
                onlyfrom.add(p);
            }
        }
        return onlyfrom;
    }

    // Lists all possible flight plan(s) from the departure point to the arrival point on the given date.
    public void printAll(String dept, String arr, String date) {

        try {
            plans = new ArrayList<FlightPlan>();
            Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            for (Airport departure: airports.get(dept)) {
                for (Airport arrival:airports.get(arr)) {
                    ArrayList<Airport> visited  = new ArrayList<Airport>();  // stores the visited Airports
                    ArrayList<Flight> pathList = new ArrayList<Flight>();    // stores the paths
                    printAllUtil(departure,arrival,d,visited,pathList,plans);
                }

            }
        }
        catch (ParseException e) {
            e.printStackTrace();

        }
    }

    public void printAllUtil(Airport dept, Airport arr,Date arrDate,ArrayList<Airport> visited,ArrayList<Flight> path,ArrayList<FlightPlan> plans){
        //if arrival point is reached
        if(dept.getId().equals(arr.getId())) {
            FlightPlan plan = new FlightPlan();
            plan.setPlan(path);
            plans.add(plan);
            return;
        }
        // mark dept and airports which is in the same city with dept as visited.
        for (Airport a: airports.get(dept.getCity())) {
            visited.add(a);
        }
        for (Flight f : flights.get(dept)){
            Airport arrival = new Airport();
            // find the arrival airport
            for(Airport a : flights.keySet()){
                if(a.getId().equals(f.getArr())) {
                    arrival = a;
                    break;
                }
            }
            if (path.isEmpty()) {
                // if the departure time of the second flight is later than the arrival time of the first flight
                if (f.getDeptDate().getTime() >= arrDate.getTime()) {
                    path.add(f);
                    printAllUtil(arrival,arr,f.getArrDate(),visited,path,plans);
                    path.remove(f);
                }
            }
            else if (!(visited.contains(arrival)) && f.getDeptDate().after(arrDate)){
                path.add(f);
                printAllUtil(arrival,arr,f.getArrDate(),visited,path,plans);
                path.remove(f);
            }
        }
        for (Airport a: airports.get(dept.getCity())) {
            visited.remove(a);
        }
    }

    // adds edges to the appropriate vertex
    public void addFlight(String[] s) {
        Flight edge = new Flight(s);
        for (Airport a: flights.keySet()) {
            if(a.getId().equals(edge.getDept())){
                flights.get(a).add(edge);
            }
        }
    }
}
