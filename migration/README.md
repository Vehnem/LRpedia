# Usage

> pip3 install virtualenv

```bash
git clone ...
cd LRpedia/migration/

virtualenv .
source bin/activate

export DATABUS_URL="https://d8lr.tools.dbpedia.org/"
export DATABUS_USER="yourusername"
export DATABUS_APIKEY="yourkey"

./migrate.py
```
