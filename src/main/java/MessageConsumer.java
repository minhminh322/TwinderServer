import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MessageConsumer {
    private ConcurrentHashMap<String, ArrayList<String>> hm_right;
    private ConcurrentHashMap<String, ArrayList<String>> hm_left;
    public MessageConsumer() {
        this.hm_right = new ConcurrentHashMap<>();
        this.hm_left = new ConcurrentHashMap<>();

    }

    public void readData(TwinderBody tb) {
        if (tb.getSwipe().equalsIgnoreCase("LEFT")) {
            hm_left.computeIfAbsent(String.valueOf(tb.getSwiper()), k -> new ArrayList<>()).add(String.valueOf(tb.getSwipee()));
        } else if (tb.getSwipe().equalsIgnoreCase("RIGHT")) {
            hm_right.computeIfAbsent(String.valueOf(tb.getSwiper()), k -> new ArrayList<>()).add(String.valueOf(tb.getSwipee()));
        } else {
            System.out.println("Invalid data");
        }
    }

    public Integer getLikeAndDislike(String userid) {
        int result = 0;

        if (hm_left.get(userid) != null) {
            result += hm_left.get(userid).size();
        }
        if (hm_right.get(userid) != null) {
            result += hm_right.get(userid).size();
        }

        return result;
    }

    public String getMatch(String userid) {
        String result = "";
        if (hm_right.get(userid) != null) {
            List<String> list = hm_right.get(userid).stream().limit(100).collect(Collectors.toList());
            result = String.join(", ", list);
        }

        return result;
    }

}
