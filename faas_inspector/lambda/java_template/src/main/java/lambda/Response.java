/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lambda;

import faasinspector.fiResponse;
import java.lang.annotation.Native;

/**
 *
 * @author wlloyd
 */
public class Response extends fiResponse {
    
    //
    // User Defined Attributes
    //
    //
    // ADD getters and setters for custom attributes here.
    //

    // Return value
    private String value;
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    
    private int calls;
    public int getCalls()
    {
        return calls;
    }
    public void setCalls(int calls)
    {
        this.calls = calls;
    }

    
    private int totalCalls;
    public int getTotalCalls()
    {
        return totalCalls;
    }
    public void setTotalCalls(int totalCalls)
    {
        this.totalCalls = totalCalls;
    }
    
    
    @Override
    public String toString()
    {
        return "value=" + this.getValue() + super.toString(); 
    }

}
