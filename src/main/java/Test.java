public class Test {
    public static void main(String[] args) {

        TwinderDao testDao = new TwinderDao();
        TwinderBody tb = new TwinderBody();
        tb.setSwipe("LEFT");
        tb.setSwiper(1);
        tb.setSwipee(2);
        tb.setComment("Test comment");
        testDao.createMessage(tb);


    }

}
