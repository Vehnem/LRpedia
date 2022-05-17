# Usage

> pip3 install virtualenv

```bash
git clone https://github.com/Vehnem/LRpedia.git
cd LRpedia/migration/

virtualenv .
source bin/activate
pip3 install -r requirements.txt

export DATABUS_URL="https://d8lr.tools.dbpedia.org/"
export DATABUS_USER="yourusername"
export DATABUS_APIKEY="yourkey"

python3 ./migrate.py
```
