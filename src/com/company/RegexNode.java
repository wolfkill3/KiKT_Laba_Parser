package com.company;

public class RegexNode {
    public String nameRegex;
    public String positiveRegex;
    public String negativeRegex;

    public RegexNode(final String nameRegex , final String positiveRegex, final String negativeRegex) {
        this.nameRegex = nameRegex;
        this.positiveRegex = positiveRegex;
        this.negativeRegex = negativeRegex;
    }
}
