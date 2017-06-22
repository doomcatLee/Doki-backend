import java.util.ArrayList;

/**
 * Created by Guest on 6/22/17.
 */
public class Conversation {

    private ArrayList<String> convoList = new ArrayList<>();

    public Conversation(){

    }


    public ArrayList<String> getConvoList() {
        return convoList;
    }

    public void addToList(String s){
        convoList.add(s);
    }

    public void setConvoList(ArrayList<String> convoList) {
        this.convoList = convoList;
    }

    public void clear(){
        convoList.clear();
    }
}
