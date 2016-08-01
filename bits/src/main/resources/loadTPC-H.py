import argparse


def parse_arguments():
        parser = argparse.ArgumentParser(description='Extract the query times from logfiles and calculate average and geometric mean')
        parser.add_argument('logfiles', metavar='logfile', type=str, nargs='+', help='enter path to logfiles or folder containing logfiles')
        args = parser.parse_args()
        logfilepaths = (args.logfiles)
