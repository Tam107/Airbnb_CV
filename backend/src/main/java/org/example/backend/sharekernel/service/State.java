package org.example.backend.sharekernel.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class State <T,V>{

    private StatusNotification status;
    private T value;
    private V error;


    public static <T,V> StateBuilder<T,V> builder(){
        return new StateBuilder<>();

    }
}
