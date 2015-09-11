package alt.flex.server;


import java.util.ArrayList;
import java.util.List;

import com.lmax.disruptor.EventFactory;

/**
 * 
 * @author Albert Shift
 *
 */

public class ValueEvent {
  private List<String> value = new ArrayList<String>();

  public void clear() {
  	value.clear();
  }
  
  public List<String> getValue() {
      return value;
  }

  public void setValue(String value) {
      this.value.add(value);
  }

  public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {
      public ValueEvent newInstance() {
          return new ValueEvent();
      }
  };
}
