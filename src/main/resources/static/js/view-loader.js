export class ViewLoader {
    load(viewName) {
        let httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', 'app/components/' + viewName + '/' + viewName + '.html', false);
        httpRequest.send();
        return httpRequest.responseText;
    }
}
export let viewLoader = new ViewLoader();