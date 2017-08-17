package com.example.vincentzhu.testapplication;

public class Recipe {
    private String name;
    private String ingredients;
    private String instructions;

    Recipe()
    {

    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setInstructions(String instructions)
    {
        this.instructions = instructions;
    }

    public void setIngredients(String ingredients)
    {
        this.ingredients = ingredients;
    }

    public String getName()
    {
        return name;
    }

    public String getInstructions() { return instructions; }

    public String getIngredients()
    {
        return ingredients;
    }
}
