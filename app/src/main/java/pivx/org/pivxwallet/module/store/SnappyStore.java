package pivx.org.pivxwallet.module.store;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import store.AddressBalance;
import store.AddressNotFoundException;
import store.AddressStore;
import store.CantInsertAddressException;


/**
 * Created by furszy on 6/14/17.
 */

public class SnappyStore implements AddressStore {

    private static final String DB_NAME = "addresses";

    private DB snappyDb;

    public SnappyStore(Context context) throws SnappydbException {
        snappyDb = DBFactory.open(context,DB_NAME);
    }

    /**
     * Insert a base58 address an his status (hash of the address history)
     *
     * @param address
     * @param status
     */
    public void insert(String address, AddressBalance status) throws CantInsertAddressException {
        try {
            snappyDb.put(address, status);
        } catch (SnappydbException e) {
            e.printStackTrace();
            throw new CantInsertAddressException("Cant insert: "+address,e);
        }
    }

    /**
     * Get the address status
     */
    public AddressBalance getAddressStatus(String address) throws AddressNotFoundException {
        try {
            return snappyDb.getObject(address,AddressBalance.class);
        } catch (SnappydbException e) {
            e.printStackTrace();
            throw new AddressNotFoundException("Cant insert: "+address,e);
        }
    }

    @Override
    public List<AddressBalance> listBalance() {
        List<AddressBalance> balances = new ArrayList<>();
        KeyIterator keyIterator = null;
        try {
            keyIterator = snappyDb.allKeysIterator();
            while (keyIterator.hasNext()){
                String[] keys = keyIterator.next(50);
                for (String key : keys) {
                    AddressBalance addressBalance = snappyDb.getObject(key,AddressBalance.class);
                    balances.add(addressBalance);
                }
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        } finally {
            if (keyIterator!=null){
                keyIterator.close();
            }
        }
        return balances;
    }


    public void close() throws SnappydbException {
        if (snappyDb!=null){
            snappyDb.close();
        }
    }

}
