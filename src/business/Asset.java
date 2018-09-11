/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

/**
 *
 * @author jasmineherd
 */
public class Asset {

    private String assetNm, emsg;
    private double cost, salvage;
    private int life;
    private boolean built;
    private double[][] begbal, anndep, endbal;
    private final int SL = 0, DDL = 1;

    public Asset(String nm, double c, double s, int lf) {
        this.assetNm = nm;
        this.cost = c;
        this.salvage = s;
        this.life = lf;
        this.built = false;

        if (isValid()) {
            build();
        }

    }

    private boolean isValid() {
        this.emsg = "";
        if (this.assetNm.isEmpty()) {
            this.emsg += "Missing Asset Name. ";
        }

        if (this.cost <= 0) {
            this.emsg += "Cost must be positive. ";

        }
        if (this.salvage < 0) {
            this.emsg += "Salvage cannot be negative. ";
        }
        if (this.salvage >= cost) {
            this.emsg += "Salvage must be less than cost. ";
        }
        if (this.life <= 0) {
            this.emsg += "Life must be positive. ";
        }

        return this.emsg.isEmpty();

    }

    private void build() {
        try {
            this.begbal = new double[this.life][2];
            this.anndep = new double[this.life][2];
            this.endbal = new double[this.life][2];

            double depSL = (this.cost - this.salvage) / this.life;
            double ddlRate = (1.0 / this.life) * 2;

            this.begbal[0][SL] = this.cost;
            this.begbal[0][DDL] = this.cost;

            for (int i = 0; i < this.life; i++) {
                if (i > 0) {
                    this.begbal[i][SL] = this.endbal[i - 1][SL];
                    this.begbal[i][DDL] = this.endbal[i - 1][DDL];
                }

                this.anndep[i][SL] = depSL;
                this.endbal[i][SL] = this.begbal[i][SL] - this.anndep[i][SL];

                double ddlWork = this.begbal[i][DDL] * ddlRate;
                if (ddlWork < depSL) {
                    ddlWork = depSL;
                }
                if ((this.begbal[i][DDL] - ddlWork) < this.salvage) {
                    ddlWork = this.begbal[i][DDL] - this.salvage;
                }
                this.anndep[i][DDL] = ddlWork;
                this.endbal[i][DDL] = this.begbal[i][DDL] - this.anndep[i][DDL];
            }

            built = true;

        } catch (Exception e) {
            this.built = false;
            this.emsg = "Exception during build " + e.getMessage();
        }
    }

    public String getErrorMsg() {
        return this.emsg;
    }

    private boolean buildok() {
        if (!this.built && isValid()) {

            build();
        }
        return this.built;
    }

    private boolean yearok(int year) {
        if (year < 1 || year > this.life) {
            this.emsg = "Year requested is out of range";
            return false;
        }
        return true;
    }

    public double getBegBal(int yr, String dtype) {
        if (!buildok() || !yearok(yr)) {
            return -1;

        }
        if (dtype.equalsIgnoreCase("S")) {
            return this.begbal[yr - 1][SL];
        } else {
            return this.begbal[yr - 1][DDL];
        }
    }

    public double getAnnDep(int yr, String dtype) {
        if (!buildok() || !yearok(yr)) {
            return -1;
        }
        if (dtype.equalsIgnoreCase("S")) {
            return this.anndep[yr - 1][SL];
        } else {
            return this.anndep[yr - 1][DDL];
        }
    }

    public double getEndBal(int yr, String dtype) {
         if(!buildok() || !yearok(yr)){
            return -1;        
        }
        if (dtype.equalsIgnoreCase("S")) {
            return this.endbal[yr - 1][SL];
        } else {
            return this.endbal[yr - 1][DDL];
        }
    }

}
