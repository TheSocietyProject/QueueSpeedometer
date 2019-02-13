public class ChatEntry {

    // if the position changes everytime, this is always 1
    // so when you r in queue at the same os for a while this number rises
    private int checks = 0;

    private int pos;

    private long time;

    private boolean saved = false;

    private ChatEntry nextEntry;

    public ChatEntry(int pos, long time){
        this.pos = pos;
        this.time = time;
    }



    public boolean isNevPos(ChatEntry nevEntry){
        boolean rV = nevEntry.pos > pos;
        this.checks ++;
        if(!rV){
            this.nextEntry = nevEntry;
        }
        return rV;
    }


    public String getData(){
        if(saved) {
            if (nextEntry != null)
                return nextEntry.getData();
            return null;
        }

        String rV = "";
        saved = true;


        rV += time + ": " + pos; // most important
        //time - pos : length

        if(nextEntry == null)
            return rV;


        return rV + "\n" + nextEntry.getData();
    }

    public String getSpeed(ChatEntry a, ChatEntry b){
        long timeDif = b.getTime() - a.getTime();
        int posDif = b.getPos() - a.getPos();
        // optional calc of speed:
        return timeDif + " / " + posDif + " = " + (timeDif / posDif);
    }





    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
