package com.example.adapteranddatamodel;

import java.util.ArrayList;

import DataModel.StateData;

public interface StateDataListener {
    void onStateDataReceived(ArrayList<StateData> stateDataList);
}
