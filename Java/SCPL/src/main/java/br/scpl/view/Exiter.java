package br.scpl.view;

import java.util.List;

import br.scpl.model.Node;

public class Exiter implements Command<Void> {

    @Override
    public Void execute(String[] args) {
        if(args.length!=0){
            System.out.println("No reason to give me parameters, anyway I'll ignore them");
        }
        System.exit(0);
        
        return null;
    }

}
