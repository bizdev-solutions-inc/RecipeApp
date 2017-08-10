package com.example.vincentzhu.testapplication;

/**
 * Created by Vincent Zhu on 8/4/2017.
 */

public class Recipe {
    String recipe_name;
    String cooking_instruction;
    String quantities;

    Recipe(String name, String qty, String instr)
    {
        recipe_name = name;
        quantities = qty;
        cooking_instruction = instr;
    }

    void setRecipeName(String in)
    {
        recipe_name = in;
    }

    void setCookingInstruction(String instr)
    {
        cooking_instruction = instr;
    }

    void setQuantities(String qty)
    {
        quantities = qty;
    }

    String getRecipeName()
    {
        return recipe_name;
    }

    String getCookingInstruction()
    {
        return cooking_instruction;
    }

    String getQuantities()
    {
        return quantities;
    }
}
