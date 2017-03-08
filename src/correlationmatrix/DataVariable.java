/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package correlationmatrix;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author srv_veralab
 */
public final class DataVariable {

    private final int id;
    private List<Double> data;

    public DataVariable(final int id) {
        this.id = id;
        this.data = new ArrayList<>();
    }

    public DataVariable(final int id, List<Double> data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DataVariable) {
            DataVariable other = (DataVariable)o;
            if (this.id == other.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.id;
        return hash;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the data
     */
    public List<Double> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<Double> data) {
        this.data = data;
    }
}
