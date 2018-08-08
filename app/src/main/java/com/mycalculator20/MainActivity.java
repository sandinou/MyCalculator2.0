package com.mycalculator20;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.common.util.NumberUtils;
import android.content.pm.ActivityInfo;



import java.text.DecimalFormat;
import java.util.EmptyStackException;
import java.util.Objects;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {


    private TextView result;
    private EditText equation;
    private String equ = "", tempEqu;
    private View view;
    private AppPreferences preferences;
    private android.support.v7.widget.Toolbar toolbar;
    private DecimalFormat df;
    private String precisionString, precision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set Activity Theme
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Set Portrait

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            equ = savedInstanceState.getString("equ");

        //set ToolBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //initialising variables
        initialiseVariables();

        //checking if first Launch
        boolean firstLauch = preferences.getBooleanPreference(AppPreferences.APP_FIRST_LAUNCH);
        if (firstLauch) {
            preferences.setBooleanPreference(AppPreferences.APP_FIRST_LAUNCH, false);
            preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "five");
        }

        //setting toolbar style manually
        setToolBarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));

        //avoiding keyboard input
        equation.setShowSoftInputOnFocus(false);
        equation.setTextIsSelectable(false);
        equation.setLongClickable(false);

        //adding text change listener
        equation.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Setting app precision */
        precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
        setPrecision(precision);
        df = new DecimalFormat(precisionString);
    }

    /* Change to valid symbol */
    private String calculateResult(String equ) {
       // StringBuffer newEqu = new StringBuffer();
        if (!equ.equals("")) {
            return getAnswer(equ);
            //return df.format(getResult(equ));
        }
        return "";

    }

    private String replace(String equ){
        StringBuffer newEqu = new StringBuffer();

        equ = equ.replace("exp(", "e^");
        //    equ = equ.replace("^", "(^");
        equ = equ.replace("x", "*");
        equ = equ.replace("÷", "/");
        newEqu.append(equ);

        for (int i=0; i<equ.length(); i++){
            if (equ.charAt(i)=='^'){
                for (int j=i-1;j>=0;j--){
                    if (isOp(String.valueOf(equ.charAt(j))) && equ.charAt(j)!='!')
                        newEqu.insert(j+1,"(");
                }
                for (int j=i+1;j<equ.length();j++){
                    if (isOp(String.valueOf(equ.charAt(j))) ){
                        newEqu.insert(j, ")");
                    }
                }

            }
        }

        System.out.println("equ = "+equ+"     newEqu = "+newEqu);
        System.out.println("new equ = "+equ.replace(equ, String.valueOf(newEqu))+"\n\n");
        equ=equ.replace(equ, String.valueOf(newEqu));
        System.out.println("\n\nequ = "+equ);

        return equ;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        String c;
        switch (id) {
            case R.id.multi:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (c .equals("%") || c.equals("!")) {
                        add("x");
                        break;
                    }

                    if (c .equals("("))
                        break;

                    if (ifPrevOperator()) {
                        if (equ.length() == 1)
                            break;

                        if (removeBackOperators())
                            add("%x");
                        else
                            add("x");

                    } else if (c.equals("."))
                        break;
                    else
                        add("x");
                }
            break;

            case R.id.div:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (c .equals("%") || c.equals("!")) {
                        add("÷");
                        break;
                    }

                    if (c.equals("("))
                        break;

                    if (ifPrevOperator()) {
                        if (equ.length() == 1)
                            break;

                        if (removeBackOperators())
                            add("%÷");
                        else
                            add("÷");

                    } else if (c.equals("."))
                        break;
                    else
                        add("÷");
                }
                break;

            case R.id.equal:
                if (!isEquationEmpty()) {
                    String res = result.getText().toString().trim();
                    if (res.equals("") || res.equals(getString(R.string.invalid_expression))) {
                        result.setText(getString(R.string.invalid_expression));
                        result.setTextColor(getResources().getColor(R.color.colorRed));
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        result.startAnimation(shake);
                        break;
                    }
                    if (!res.equals("")) {
                        equ=res;
                        equation.setText(res);
                        result.setText("");
                    }
                }
            break;

            case R.id.del:
                if (!isEquationEmpty()) {
                    equ = equ.substring(0, equ.length() - 1);
                    equation.setText(equ);
                } else
                    equation.setText("");
            break;

            case R.id.c:
                if (!equation.getText().toString().equals(""))
                    animateClear(view);
                equ = "";
                equation.setText(equ);
                result.setText("");
            break;

            case R.id.point:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") && canPlaceDecimal()) {
                        add(".");
                        break;
                    }
                    if (ifPrevOperator() && !c.equals("%")) {
                        add("0.");
                        break;
                    }
                    if (c.equals("(")) {
                        add("0.");
                        break;
                    }
                    if (isSymbol(c)) {
                        add("x0.");
                        break;
                    }
                } else if (isEquationEmpty())
                    add("0.");
            break;

            case R.id.plus:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("+");
                        break;
                    }
                    if (c.equals("("))
                        break;

                    if (c.equals(".")){
                        add("0+");
                        break;
                    }
                    if (isOperator(c)) {
                        if (removeBackOperators())
                            add("%+");
                        else
                            add("+");
                        break;
                    }

                    if (!c.equals(".") && !isOperator(c))
                        add("+");
                    else if (isOperator(c)) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("+");
                    }
                    break;
                }
                if ((!ifPrevOperator()) || equ.equals(""))
                    add("+");
                else if (ifPrevOperator()) {
                    if (!isEquationEmpty()) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("+");
                    }
                }
            break;

            case R.id.minus:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("-");
                        break;
                    }
                    if (c.equals("("))
                        break;

                    if (c.equals(".")){
                        add("0-");
                        break;
                    }

                    if (isOperator(c)) {
                        if (equ.length() >= 2 && (isNumber(equ.charAt(equ.length() - 2) + ""))) {
                            if (c.equals("-")) {
                                removeBackOperators();
                                add("+");
                                break;
                            }
                            add("-");
                            break;
                        }
                        if (removeBackOperators())
                            add("%-");
                        else
                            add("-");
                        break;
                    }

                    if (!c.equals(".") && !isOperator(c))
                        add("-");
                    else if (isOperator(c))
                        if (c.equals("+") || c.equals("÷") || c.equals("x"))
                            add("-");
                    break;
                }
                if (!ifPrevOperator() || equ.equals(""))
                    add("-");
                else if (ifPrevOperator()) {
                    if (!isEquationEmpty()) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("-");
                    }
                }
            break;

            case R.id.one:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x1");
                        break;
                    }
                }
                add("1");
            break;

            case R.id.two:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x2");
                        break;
                    }
                }
                add("2");
            break;

            case R.id.three:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x3");
                        break;
                    }
                }
                add("3");
            break;

            case R.id.four:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x4");
                        break;
                    }
                }
                add("4");
            break;

            case R.id.five:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x5");
                        break;
                    }
                }
                add("5");
            break;

            case R.id.six:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x6");
                        break;
                    }
                }
                add("6");
            break;

            case R.id.seven:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x7");
                        break;
                    }
                }
                add("7");
            break;

            case R.id.eight:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c) ) {
                        add("x8");
                        break;
                    }
                }
                add("8");
            break;

            case R.id.nine:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c) ) {
                        add("x9");
                        break;
                    }
                }
                add("9");
            break;

            case R.id.zero:
                if(!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c)) {
                        add("x0");
                        break;
                    }
                }
                add("0");
            break;

            case R.id.percent:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add("%");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1)
                            break;
                        removeBackOperators();
                        if (!isEquationEmpty())
                            add("%");
                        break;
                    }
                    if (c.equals("."))
                        add("0%");
                }
            break;

            case R.id.open:
                if (!isEquationEmpty() && equ.charAt(equ.length() - 1) == '.') {
                    equ = equ.substring(0, equ.length() - 1);
                    add("x(");
                    break;
                }

                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add("x(");
                        break;
                    }
                    if (c.equals("(") || isOperator(c)) {
                        add("(");
                        break;
                    }
                } else
                    add("(");
            break;

            case R.id.close:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add(")");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0)");
                        break;
                    }
                    if (c.equals("(")) {
                        equ = equ.substring(0, equ.length() - 1);
                        equation.setText(equ);
                        break;
                    }
                }
            break;

            case R.id.pi:
                String PI2 = "π";
                if (!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c) || isNumber(""+c)){
                        add("x"+ PI2);
                        break;
                    }
                }
                add(PI2);
            break;

            case R.id.x2:
                if (!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add("^(2)");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1)
                            break;
                        removeBackOperators();
                        if (!isEquationEmpty())
                            add("^(2)");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0^(2)");
                        break;
                    }
                }
            break;

            case R.id.x3:
                if (!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add("^(3)");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1)
                            break;
                        removeBackOperators();
                        if (!isEquationEmpty())
                            add("^(3)");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0^(3)");
                        break;
                    }
                }
                break;

            case R.id.xn:
                if (!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add("^(");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1)
                            break;
                        removeBackOperators();
                        if (!isEquationEmpty())
                            add("^(");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0^(");
                        break;
                    }
                }
                break;

            case R.id._10x:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(""+c)){
                        add("x10^(");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0x(10^(");
                        break;
                    }

                    if (isOp(c)) {
                        add("(10^(");
                        break;
                    }

                    add("10^(");
                    break;
                }
                else
                    add("10^(");
                break;

                /*
                  if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c) || isNumber(""+c)){
                        add("x(exp(");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0x(exp(");
                        break;
                    }
                }
                add("exp(");
                break;
                 */

            case R.id.factorial:
                if (!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isNumber(c + "") || isSymbol(c)) {
                        add("!");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1)
                            break;
                        removeBackOperators();
                        if (!isEquationEmpty())
                            add("!");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0!");
                        break;
                    }
                }
                break;

            case R.id.e:
                String E = "e";
                if (!isEquationEmpty()){
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if ( isSymbol(c) || isNumber(""+c) ){
                        add("x"+ E);
                        break;
                    }
                }
                add(E);
                break;

            case R.id.ex:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c) || isNumber(""+c)){
                        add("xexp(");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0xexp(");
                        break;
                    }
                }
                add("exp(");
                break;

            case R.id.log:
                if (!isEquationEmpty()) {
                    c = String.valueOf(equ.charAt(equ.length() - 1));
                    if (isSymbol(c) || isNumber(""+c)){
                        add("xlog(");
                        break;
                    }
                    if (c.equals(".")) {
                        add("0xlog(");
                        break;
                    }
                }
                else
                    add("log(");
                break;

            default:
                break;
        }
    }


    private boolean isSymbol(String c){
        if (c.equals(")") || c.equals("π") || c.equals("%") || c.equals("!") || c.equals("e")){
            return true;
        }
        else
            return false;
    }



    private boolean removeBackOperators() {
        boolean value = false;
        if (!isEquationEmpty()) {
            String c = String.valueOf(equ.charAt(equ.length() - 1));
            while (isOperator(c)) {
                if (c.equals("%") || c.equals("^"))
                    value = true;
                equ = equ.substring(0, equ.length() - 1);
                if (equ.length() == 0)
                    break;
                c = String.valueOf(equ.charAt(equ.length() - 1));
            }
        }
        equation.setText(equ);
        return value;
    }

    private void add(String str) {
        if (isEquationEmpty())
            equ = "";

        equ += str;
        equation.setText(equ);

    }

    private boolean isEquationEmpty() {
        String eq = equ;
        if (eq.equals(""))
            return true;
        if (eq == null)
            return true;
        return false;
    }

    private String getAnswer(String s) {
        String c = String.valueOf(s.charAt(s.length() - 1));

        while (isOperator(c) && !c.equals("%") && !c.equals("!")) {
            s = s.substring(0, s.length() - 1);
            if (!s.equals(""))
                c = String.valueOf(s.charAt(s.length() - 1));
            else
                return "";
        }

        Stack<String> stack = new Stack<>();
        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            c = String.valueOf(s.charAt(i));

            if (isOperator(c)) {
                if (!temp.equals(""))
                    stack.push(temp);
                stack.push(c + "");
                temp = "";
            } else if (c.equals("(")) {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                stack.push("(");
            } else if (c.equals(")")) {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                String abc = "";
                while (!stack.peek().equals("(")) {
                    abc = stack.pop() + abc;
                }
                stack.pop();
                stack.push(df.format(getValue(abc)));
            }
            else
                temp = temp + c;

        }

        if (!temp.equals(""))
            stack.push(temp);

        String lll = "";
        while (!stack.empty())
            lll = stack.pop() + lll;


        if(df==null){
            //setting app precision
            precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
            setPrecision(precision);
            df = new DecimalFormat(precisionString);
        }

        try {
            return df.format(getValue(lll));
        } catch (NumberFormatException e) {
            String res = getResources().getString(R.string.invalid_expression)+"!!";
            result.setTextColor(getResources().getColor(R.color.colorRed));
            return res;
        } catch (EmptyStackException e){
            String res = getResources().getString(R.string.invalid_expression)+"!!";
            result.setTextColor(getResources().getColor(R.color.colorRed));
            return res;
        }
    }


    private double getValue(String equations) {

        String c = String.valueOf(equations.charAt(equations.length() - 1));

        while (isOperator(c) && !c.equals("%") && !c.equals("!")) {
            equations = equations.substring(0, equations.length() - 1);
            if (!equations.equals(""))
                c = String.valueOf(equations.charAt(equations.length() - 1));
        }

        String temp = "";
        Stack<String> stack = new Stack<>();
        Stack<String> workingStack = new Stack<>();

      /*  if (!equations.startsWith("("))
            equations = "0" + equations;*/
        //equations = equations.replaceAll("-", "+-");
      /*  equations = equations.replaceAll("(\\*\\+)", "*");
        equations = equations.replaceAll("(\\/\\+)", "/");
        equations = equations.replaceAll("(\\+\\+)", "+");*/
        equations = equations.replaceAll("π", String.valueOf(Math.PI));
        equations = equations.replaceAll("e", String.valueOf(Math.E));
      //  equations = equations.replaceAll("(\\*\\+)", "*");

       // equations = equations.replaceAll("log")

        //tokenize
        temp = "";
        for (int i = 0; i < equations.length(); i++) {
            c = String.valueOf(equations.charAt(i));

            if (isOp(c)) {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                stack.push(c + "");
            } else
                temp = temp + c;
        }

        if (!temp.equals(""))
            stack.push(temp);

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();

            return Double.parseDouble(ans);
        }

        //****  unary operator
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            double num1;
            switch (temp){
                case "%":
                    String val1 = stack.pop();
                    num1 = Double.parseDouble(val1);
                    num1 = num1 / 100;
                    val1 = num1 + "";
                    stack.push(val1);
                break;

                case "!":
                    String val2 = stack.pop();
                    int num2 = Integer.parseInt(val2);
                    if (num2<0 || val2.contains(".")) {
                        result.setText(getString(R.string.invalid_expression));
                        break;
                    }
                    else{
                        int fact=1;
                        for (int i = 1; i <= num2; i++)
                            fact = fact*i;
                        val2 = fact+"";
                        stack.push(val2);
                    }
                break;

                default:
                    stack.push(temp);
                break;
            }
        }

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }

        /******************************************************************/
        // **** multiplication
        String abc;
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            abc= String.valueOf(temp.charAt(0));

            if (temp.length() == 1 && abc.equals("*")) {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 * num2;
                val1 = num1 + "";
                stack.push(val1);
            } else
                stack.push(temp);
        }

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }
        /******************************************************************/

        // **** division
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            abc= String.valueOf(temp.charAt(0));
            if (temp.length() == 1 &&  abc.equals("/")) {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 / num2;
                val1 = num1 + "";
                stack.push(val1);
            } else
                stack.push(temp);
        }

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }
        /******************************************************************/

        // **** power
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            abc= String.valueOf(temp.charAt(0));
            if (temp.length() == 1 && abc.equals("^")) {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                //num1 = num1 * num2;
                num1 = Math.pow(num1,num2);
                val1 = num1 + "";
                stack.push(val1);
            } else
                stack.push(temp);
        }

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }
        /******************************************************************/

        // **** soustraction
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            abc= String.valueOf(temp.charAt(0));
            if (temp.length() == 1 && abc.equals("-")) {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 - num2;
                val1 = num1 + "";
                stack.push(val1);
            } else
                stack.push(temp);
        }

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }
        /******************************************************************/

        // **** addition
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            abc= String.valueOf(temp.charAt(0));
            if (temp.length() == 1 && abc.equals("+")) {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 + num2;
                val1 = num1 + "";
                stack.push(val1);
            } else
                stack.push(temp);
        }

        while (!stack.empty())
            workingStack.push(stack.pop());

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        } else
            return 0.0;

    }

    private boolean isOp(String c) {
        if (c.equals("+") || c.equals("-")|| c.equals("/") || c.equals("*") || c.equals("%")  || c.equals("!") || c.equals("^"))
            return true;
        return false;
    }

    private boolean ifPrevOperator() {
        if (equ.equals(""))
            return true;
        String c = String.valueOf(equ.charAt(equ.length() - 1));
        return isOperator(c);
    }

    private boolean isOperator(String c) {
        if (c.equals("+") || c.equals("-") || c.equals("/") || c.equals("*") || c.equals("÷") || c.equals("x")  || c.equals("%")  || c.equals("^") )
            return true;
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("equ", equ);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        equ = savedInstanceState.getString("equ");

        if (balancedParenthesis(equ)) {
            result.setTextColor(getTextColor());
            result.setText(calculateResult(equ));
        } else {
            //trying to balance equation
            // this is smart calculator
            tryBalancingBrackets();
            //if could balance the equation, calculate the result
            if (balancedParenthesis(tempEqu)) {
                //calculate result
                result.setTextColor(getTextColor());
                result.setText(calculateResult(tempEqu));
            } else
                result.setText("");
        }
    }

    private boolean canPlaceDecimal() {
        String eq = equ;
        int j = eq.length() - 1;
        int count = 0;
        while (j >= 0 && !isOperator(String.valueOf(eq.charAt(j)))) {
            if (eq.charAt(j) == '.')
                count++;
            j--;
        }
        if (count == 0)
            return true;
        else
            return false;
    }

    private void animateClear(View viewRoot) {
        int cx = viewRoot.getRight();
        int cy = viewRoot.getBottom();
        int l = viewRoot.getHeight();
        int b = viewRoot.getWidth();
        int finalRadius = (int) Math.sqrt((l * l) + (b * b));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
            viewRoot.setVisibility(View.VISIBLE);
            anim.setDuration(300);
            anim.addListener(listener);
            anim.start();
        }
    }

    private Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            view.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        equ=replace(equ);
        if (balancedParenthesis(equ)) {
            result.setTextColor(getTextColor());
            result.setText(calculateResult(equ));
        } else {
            //trying to balance equation
            tryBalancingBrackets();
            //if could balance the equation, calculate the result
            if (balancedParenthesis(tempEqu)) {
                //calculate result
                result.setTextColor(getTextColor());
                result.setText(calculateResult(tempEqu));
            } else
                result.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int resId = item.getItemId();
        Intent intent;

        switch (resId) {
            case R.id.settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            break;

            case R.id.exit:
                finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private boolean isNumber(String str) {
        return NumberUtils.isNumeric(str);
    }

    private boolean balancedParenthesis(String s) {
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(')
                stack.push(c);
            else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(')
                    return false;
            }
        }
        return stack.isEmpty();
    }

    private void tryBalancingBrackets() {
        tempEqu = equ;
        int a = 0, b = 0;

        for (int i = 0; i < tempEqu.length(); i++) {
            char c = tempEqu.charAt(i);
            if (c == '(')
                a++;
            if (c == ')')
                b++;
        }

        if (a != b) {
            int num = -1;
            if (a > b) {
                num = a - b;
                char c = tempEqu.charAt(tempEqu.length() - 1);
                if (isNumber(c + "") || c == ')' || c == '%') {
                    tempEqu = tempEqu + ")";
                    num--;

                } else if (c == '.') {
                    tempEqu = tempEqu + "0)";
                    num--;
                } else if (c == '(')
                    return;

                while (num > 0) {
                    tempEqu = tempEqu + ")";
                    num--;
                }
            }
            if (a < b) {
                num = b - a;
                while (num > 0) {
                    tempEqu = "(" + tempEqu;
                    num--;
                }
            }
        }
    }

    private void setTheme(String themeName) {

        switch (themeName){
            case "turquoise":
                setTheme(R.style.TurquoiseAppTheme);
                break;
            case "orange":
                setTheme(R.style.AppTheme);
                break;
            case "blue":
                setTheme(R.style.BlueAppTheme);
                break;
            case "green":
                setTheme(R.style.GreenAppTheme);
                break;
            case "pink":
                setTheme(R.style.PinkAppTheme);
                break;
            case "default":
                setTheme(R.style.DefAppTheme);
                break;
            case "":
                setTheme(R.style.DefAppTheme);
                preferences.setStringPreference(AppPreferences.APP_THEME,"default");
                break;
        }
    }

    private int getTextColor() {
        String theme = preferences.getStringPreference(AppPreferences.APP_THEME);

        switch (theme){
            case "turquoise":
                return getResources().getColor(R.color.colorTurquoiseDark);
            case "orange":
                return getResources().getColor(R.color.colorPrimary);
            case "blue":
                return getResources().getColor(R.color.colorBlueDark);
            case "green":
                return getResources().getColor(R.color.colorLightGreenDark);
            case "pink":
                return getResources().getColor(R.color.colorPinkDark);
            default:
                return getResources().getColor(R.color.darkGray);
        }

    }

    public void setPrecision(String precision) {
        if (precision.equals("")) {
            precisionString = "#.#####";
            preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "five");
        } else {
            switch (precision) {
                case "two":
                    precisionString = "#.##";
                    break;
                case "three":
                    precisionString = "#.###";
                    break;
                case "four":
                    precisionString = "#.####";
                    break;
                case "five":
                    precisionString = "#.#####";
                    break;
                case "six":
                    precisionString = "#.######";
                    break;
                case "seven":
                    precisionString = "#.#######";
                    break;
                case "eight":
                    precisionString = "#.########";
                    break;
                case "nine":
                    precisionString = "#.#########";
                    break;
                case "ten":
                    precisionString = "#.##########";
                    break;
                default:
                    precisionString = "#.######";
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setToolBarStyle(String themeName) {
        switch (themeName){
            case "turquoise":
                Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(getResources().getColor(R.color.colorTurquoiseDark));
                break;
            case "orange":
                Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case "blue":
                Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(getResources().getColor(R.color.colorBlueDark));
                break;
            case "green":
                Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(getResources().getColor(R.color.colorLightGreenDark));
                break;
            case "pink":
                Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(getResources().getColor(R.color.colorPinkDark));
                break;
            default:
                Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(getResources().getColor(R.color.darkGray));
                break;
        }
    }

    private void initialiseVariables() {
        //Initialisations
        View mainLayout = findViewById(R.id.mainLayout);
        equation = findViewById(R.id.calculET);
        result = findViewById(R.id.resultTV);
        Button b1 = mainLayout.findViewById(R.id.one);
        Button b2 = mainLayout.findViewById(R.id.two);
        Button b3 = mainLayout.findViewById(R.id.three);
        Button b4 = mainLayout.findViewById(R.id.four);
        Button b5 = mainLayout.findViewById(R.id.five);
        Button b6 = mainLayout.findViewById(R.id.six);
        Button b7 = mainLayout.findViewById(R.id.seven);
        Button b8 = mainLayout.findViewById(R.id.eight);
        Button b9 = mainLayout.findViewById(R.id.nine);
        Button b0 = mainLayout.findViewById(R.id.zero);
        Button badd = mainLayout.findViewById(R.id.plus);
        Button bsub = mainLayout.findViewById(R.id.minus);
        Button bmul = mainLayout.findViewById(R.id.multi);
        Button bdiv = mainLayout.findViewById(R.id.div);
        Button bequal = mainLayout.findViewById(R.id.equal);
        ImageButton bdel = mainLayout.findViewById(R.id.del);
        Button c = mainLayout.findViewById(R.id.c);
        Button pi = mainLayout.findViewById(R.id.pi);
        Button bpower2 = mainLayout.findViewById(R.id.x2);
        Button bpower3 = mainLayout.findViewById(R.id.x3);
        Button bpower = mainLayout.findViewById(R.id.xn);
        Button bdecimal = mainLayout.findViewById(R.id.point);
        Button open = mainLayout.findViewById(R.id.open);
        Button close = mainLayout.findViewById(R.id.close);
        Button percent = mainLayout.findViewById(R.id.percent);
        Button b10x = mainLayout.findViewById(R.id._10x);
        Button factorial = mainLayout.findViewById(R.id.factorial);
        Button e = mainLayout.findViewById(R.id.e);
        Button ex = mainLayout.findViewById(R.id.ex);
        Button log = mainLayout.findViewById(R.id.log);

        view = findViewById(R.id.view);

        //adding onClickListeners
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        b0.setOnClickListener(this);
        badd.setOnClickListener(this);
        bsub.setOnClickListener(this);
        bmul.setOnClickListener(this);
        bdiv.setOnClickListener(this);
        bequal.setOnClickListener(this);
        bdel.setOnClickListener(this);
        c.setOnClickListener(this);
        bdecimal.setOnClickListener(this);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        percent.setOnClickListener(this);
        pi.setOnClickListener(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bpower2.setOnClickListener(this);
            bpower3.setOnClickListener(this);
            bpower.setOnClickListener(this);
            b10x.setOnClickListener(this);
            factorial.setOnClickListener(this);
            e.setOnClickListener(this);
            ex.setOnClickListener(this);
            log.setOnClickListener(this);
        }

    }
}
