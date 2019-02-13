import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.reminecraft.api.RePlugin;
import com.sasha.reminecraft.api.event.ChatReceivedEvent;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.reminecraft.logging.ILogger;
import com.sasha.reminecraft.logging.LoggerBuilder;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main  extends RePlugin implements SimpleListener {

    private ArrayList<ChatEntry> data = new ArrayList<>();



    public final String queueMsg = "Position in queue: ";

    private boolean inQueue = true;

    public ILogger logger = LoggerBuilder.buildProperLogger("QueueSpeedometerPlugin");

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);


    @Override
    public void onPluginInit() {
        this.getReMinecraft().EVENT_BUS.registerListener(this);
    }

    @Override
    public void onPluginEnable() {
        executor.scheduleAtFixedRate(() -> {
            if (ReClient.ReClientCache.INSTANCE.playerListEntries.size() != 0) {
                writeToFile();
            }
        }, 5L, 60L, TimeUnit.SECONDS);
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public void onPluginShutdown() {

    }

    @Override
    public void registerCommands() {

    }

    @SimpleEventHandler
    public void onEvent(ChatReceivedEvent e){
        String msg = e.getMessageText();
        if(!checkQueue(msg)) {
            this.getReMinecraft().reLaunch(); // to reenter the queue
            return;
        }

        if(!msg.startsWith(queueMsg))
            return;



        ChatEntry cur = new ChatEntry(Integer.parseInt(msg.substring(queueMsg.length())), e.getTimeRecieved());

        if(data.size() == 0) {
            data.add(cur);
            return;
        }

        if(data.get(data.size() - 1).isNevPos(cur))
            data.add(cur);




        /*

            have an array with time + pos
            then if pos changed calc last length


            save: time - pos : length
            and then next time it changed

            ^ chatSpeed


    but I wouild like to have first hand changes
    so I have to test for a new pos in tab every <sec... :/

         */


    }


    public boolean checkQueue(String msg){
        if (msg.startsWith("<")) {
            return inQueue = false;
        }

        if (msg.startsWith("2b2t is full")) {
            return inQueue = true;
        }

        if(msg.startsWith(queueMsg)){
            return inQueue = true;
        }

        return this.isQueue();
    }

    public boolean isQueue(){
        return this.inQueue;
    }



    /**
     *  Method to be called at a fixed rate,
     *  writes everything to a file and switches to a new file if needed
     */
    private void writeToFile() {


        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

        String filename = s + "/data/QueueSpeedoMeterLog" + LocalDateTime.now().getYear() + "-" + LocalDateTime.now().getMonthValue() + "-" + LocalDateTime.now().getDayOfMonth() + ".txt";
        logger.log("[SpeedLogger]: flushing data: " + data.size() + " in " + filename);
        try {

            File f = new File(filename);
            if(!f.exists()){
                new File(s + "\\data").mkdir();//.createNewFile();
            }
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (ChatEntry cE : data) {
                String line = cE.getData();
                if(line != null) {
                    bw.newLine();
                    bw.write(line);
                    bw.flush();
                }
            }
            ChatEntry last = data.get(data.size() - 1);
            data = new ArrayList<>();
            data.add(last);

            fw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }



    @Override
    public void registerConfig() {

    }
}
