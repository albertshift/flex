package alt.flex.client.api.operation;

import alt.flex.client.internal.op.AbstractSingleOp;

/**
 * 
 * @author Albert Shift
 *
 */

public interface BatchOperation extends AbstractOperation<BatchOperation, Object[]>  {

	BatchOperation add(AbstractSingleOp<?, ?> singleOp);
	
}
