public abstract class Statement_ast {
	public abstract boolean typeChecking(SymbolList list) throws Exception;
	public abstract void draw(int p);
	public abstract int stmtType();
}
