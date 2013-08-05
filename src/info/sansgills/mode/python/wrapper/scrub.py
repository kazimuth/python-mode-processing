#script to clean up last run

for key in globals().copy():
	if key != '__builtins__':
		del globals()[key]