import '@testing-library/jest-dom';

if (!window.matchMedia) {
  window.matchMedia = () => ({
    matches: false,
    addListener() {},
    removeListener() {},
    addEventListener() {},
    removeEventListener() {},
    dispatchEvent() {
      return false;
    },
  });
}

HTMLCanvasElement.prototype.getContext = () => ({
  fillRect() {},
  clearRect() {},
  getImageData() {
    return { data: [] };
  },
  putImageData() {},
  createImageData() {
    return [];
  },
  setTransform() {},
  drawImage() {},
  save() {},
  fillText() {},
  restore() {},
  beginPath() {},
  moveTo() {},
  lineTo() {},
  closePath() {},
  stroke() {},
  translate() {},
  scale() {},
  rotate() {},
  arc() {},
  fill() {},
  measureText() {
    return { width: 0 };
  },
  transform() {},
  rect() {},
  clip() {},
});

const originalGetComputedStyle = window.getComputedStyle?.bind(window);

window.getComputedStyle = (element, pseudoElt) => {
  if (pseudoElt) {
    return {
      getPropertyValue() {
        return '';
      },
    };
  }

  if (originalGetComputedStyle) {
    return originalGetComputedStyle(element);
  }

  return {
    getPropertyValue() {
      return '';
    },
  };
};
