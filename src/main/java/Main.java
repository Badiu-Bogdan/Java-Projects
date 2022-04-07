import service.*;
import ui.Console;
import ui.ConsoleException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String args[]){

        Console console = new Console();
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            console.runConsole(bufferRead);
        }catch(ConsoleException e){
            System.out.println(e.getMessage());
        }

        System.out.println("RowerPangers 2021");
    }
}
