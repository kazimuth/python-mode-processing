#script to clean up last run
for key,val in g.iteritems():
	if key not in {'__name__', '__doc__', '__package__', '__builtins__'}:
		del g[key]