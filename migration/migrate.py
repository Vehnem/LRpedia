#!/usr/bin/env python3

import os, json
from databusclient import deploy
import pandas as pd


#deploy.createDataset
#deploy.deploy


# "a6f1-49d0369df3c4-a49c-c670bb558dfc"
DATABUS_APIKEY = os.environ['DATABUS_APIKEY']
DATABUS_USER = os.environ['DATABUS_USER']
DATABUS_URL= str(os.environ['DATABUS_URL']).strip('/')
DATABUS_GROUP = "llod"
DATABUS_DEFAULTVERSION  = "1.0.10-alpha"

print(f"deploy all to: {DATABUS_URL}/{DATABUS_USER}/{DATABUS_GROUP}")
print("---")

# sheet_id = "1j72p6HUGGUh3peduictrhjDqo3JaJLFbVs7-4aywHHc"
# sheet_name = "relevant-datasets"
# url = f"https://docs.google.com/spreadsheets/d/{sheet_id}/gviz/tq?tqx=out:csv&sheet={sheet_name}"
# url = f"https://docs.google.com/spreadsheets/d/{sheet_id}/export?exportFormat&csvsheet={sheet_name}"
sheet_url = "https://docs.google.com/spreadsheets/d/1j72p6HUGGUh3peduictrhjDqo3JaJLFbVs7-4aywHHc/export?exportFormat=csv&gid=1589516383"

df = pd.read_csv(sheet_url, sep=",")
df.reset_index

for idx, row in df.iterrows():

    title = row["atifact_label"]
    artifactName = row['atifact_id']
    version = row['version']
    abstract = title
    description = row['ducumentation']
    contentVariants  = str(row['content_variant']).replace(";","_")

    print("DEBUG", title)

    license = row['license']

    url = row['url']

    dataset = deploy.createDataset(
        f"{DATABUS_URL}/{DATABUS_USER}/{DATABUS_GROUP}/{artifactName}/{version}",
        title, abstract, description,
        license,
        [url+"|"+contentVariants]   
    )

    # print(json.dumps(dataset))#, indent=2, sort_keys=True))
    deploy.deploy(dataset,DATABUS_APIKEY)
    print("---")
    # print()
