import json
import os
import xml.etree.ElementTree as ET
from pathlib import Path

import httpx

BRANCH = os.getenv("BRANCH", "development")
PR_NUMBER = os.getenv("PR_NUMBER", "0")
REPO = os.getenv("REPO_NAME", "ArthurKun21/fga")


GITHUB_TOKEN = os.getenv("GITHUB_TOKEN", "")
if len(GITHUB_TOKEN) > 0:
    headers = {
        "Authorization": f"token {GITHUB_TOKEN}",
        'Accept': 'application/vnd.github.v3+json',
    }
else:
    headers = None

URL_BASE = f"https://api.github.com/repos/{REPO}"


ESCAPE_CHARACTER = "<ln>\n"

cwd = Path.cwd()


def save_releases_summary(summary: str):
    strings_xml_path = cwd / "app" / "src" / "main" / "res" / "values" / "strings.xml"
    tree = ET.parse(strings_xml_path)
    root = tree.getroot()

    element = root.find("./string[@name='release_notes_summary']")
    if element is not None:
        element.text = f'"{summary}"'
        tree.write(strings_xml_path)


def get_commit_message(sha: str):
    commit_url = f"{URL_BASE}/git/commits/{sha}"
    response = httpx.get(commit_url, headers=headers)
    data = json.loads(response.text)
    message = data["message"]
    return message


def get_latest_commit():
    last_commit = f"{URL_BASE}/commits"
    response = httpx.get(last_commit, headers=headers)
    data = json.loads(response.text)
    last_commit_sha = data[0]["sha"]
    message = get_commit_message(last_commit_sha)
    return message


def url_tag_commit_sha(tag: str):
    URL_TAG = f"{URL_BASE}/git/refs/tags/{tag}"
    response = httpx.get(URL_TAG, headers=headers)
    data = json.loads(response.text)
    return data["object"]["sha"]


def get_last_releases():
    last_releases = f"{URL_BASE}/releases"
    response = httpx.get(last_releases, headers=headers)
    body = json.loads(response.text)

    commit_info_list = []

    for release in body:
        tag = release["tag_name"]

        tag_sha = url_tag_commit_sha(tag)

        tag_commit_message = get_commit_message(tag_sha)

        commit_info_list.append(f"{tag}")
        commit_info_list.append(f"{tag_commit_message}")
    return commit_info_list


def development_branch():
    latest_commit = get_latest_commit()
    last_releases_information = get_last_releases()
    information = ["Latest", f"{latest_commit}"] + last_releases_information

    release_notes_summary = f"{ESCAPE_CHARACTER}".join(information)

    save_releases_summary(release_notes_summary)


def get_pr_title_and_url():
    URL_PR = f"{URL_BASE}/pulls/{PR_NUMBER}"
    response = httpx.get(URL_PR, headers=headers)
    data = json.loads(response.text)
    return data["title"], data["html_url"]


def pull_request_branch():
    pr_title, pr_url = get_pr_title_and_url()
    URL_PR_COMMITS = f"{URL_BASE}/pulls/{PR_NUMBER}/commits"
    response = httpx.get(URL_PR_COMMITS, headers=headers)
    pull_request_commits_data = json.loads(response.text)

    commit_info_list = []
    commit_info_list.append("PR")
    commit_info_list.append(f"{pr_title}")
    commit_info_list.append(f"{pr_url}")

    print(f"PR: {pr_title}")

    for data in pull_request_commits_data:
        message = data["commit"]["message"]
        commit_info_list.append(message)

    release_notes_summary = f"{ESCAPE_CHARACTER}".join(commit_info_list)

    save_releases_summary(release_notes_summary)


def main():
    if BRANCH == "development":
        print("development branch")
        development_branch()
    else:
        print(f"pull request branch {PR_NUMBER}")
        pull_request_branch()


if __name__ == "__main__":
    main()
