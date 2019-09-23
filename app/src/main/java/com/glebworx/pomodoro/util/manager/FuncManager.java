package com.glebworx.pomodoro.util.manager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

public class FuncManager {

    private static FuncManager funcManager = new FuncManager();
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();

    private FuncManager() { }

    public static FuncManager getInstance() {
        return funcManager;
    }

    /*private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);

        return functions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }*/

}
